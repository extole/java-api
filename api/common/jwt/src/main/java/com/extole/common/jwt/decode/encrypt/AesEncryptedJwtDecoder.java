package com.extole.common.jwt.decode.encrypt;

import java.security.Key;
import java.text.ParseException;

import javax.crypto.SecretKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jwt.EncryptedJWT;

import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InvalidDecodingKeyAlgorithmException;
import com.extole.common.jwt.decode.InvalidDecodingKeyTypeException;
import com.extole.common.jwt.decode.InvalidSecuredJwtDecoderException;
import com.extole.common.jwt.decode.MissingDecodingKeyException;

final class AesEncryptedJwtDecoder implements InternalSecuredJwtDecoder<EncryptedUncheckedJwt> {

    private final SecretKey secretKey;

    private AesEncryptedJwtDecoder(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public EncryptedJwt toDecodedObject(EncryptedUncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        JWEDecrypter decrypter = createAesDecrypter(secretKey);
        try {
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(uncheckedJwt.getEncodedJwtString());
            encryptedJWT.decrypt(decrypter);
            return new EncryptedJwt(uncheckedJwt.getHeader(), encryptedJWT.getPayload().toJSONObject());
        } catch (ParseException e) {
            throw new JwtRuntimeException("Could not parse encoded JWT", e);
        } catch (JOSEException e) {
            throw new InvalidSecuredJwtDecoderException("Could not decrypt JWT", e);
        }
    }

    private JWEDecrypter createAesDecrypter(SecretKey secretKey) {
        try {
            return new AESDecrypter(secretKey);
        } catch (KeyLengthException e) {
            throw new JwtRuntimeException("Could not decrypt JWT", e);
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements InternalSecuredJwtDecoderBuilder<EncryptedUncheckedJwt> {

        private SecretKey secretKey;

        private Builder() {
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            validateKey(key);
            this.secretKey = (SecretKey) key;
            return this;
        }

        @Override
        public AesEncryptedJwtDecoder build() throws MissingDecodingKeyException {
            if (secretKey == null) {
                throw new MissingDecodingKeyException();
            }
            return new AesEncryptedJwtDecoder(secretKey);
        }

        private static void validateKey(Key key)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            if (!(key instanceof SecretKey)) {
                throw new InvalidDecodingKeyTypeException(key.getClass(),
                    EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS);
            }
            try {
                new AESDecrypter((SecretKey) key);
            } catch (JOSEException e) {
                throw new InvalidDecodingKeyAlgorithmException(key.getAlgorithm(),
                    EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS, e);
            }
        }
    }
}
