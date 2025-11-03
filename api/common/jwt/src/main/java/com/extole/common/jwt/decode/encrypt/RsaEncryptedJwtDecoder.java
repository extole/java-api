package com.extole.common.jwt.decode.encrypt;

import java.security.Key;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;

import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InvalidDecodingKeyAlgorithmException;
import com.extole.common.jwt.decode.InvalidDecodingKeyTypeException;
import com.extole.common.jwt.decode.InvalidSecuredJwtDecoderException;
import com.extole.common.jwt.decode.MissingDecodingKeyException;

final class RsaEncryptedJwtDecoder implements InternalSecuredJwtDecoder<EncryptedUncheckedJwt> {

    private final RSAPrivateKey privateKey;

    private RsaEncryptedJwtDecoder(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public EncryptedJwt toDecodedObject(EncryptedUncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        JWEDecrypter decrypter = new RSADecrypter(privateKey);
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

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements InternalSecuredJwtDecoderBuilder<EncryptedUncheckedJwt> {

        private RSAPrivateKey key;

        private Builder() {
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            validateKeyType(key);
            this.key = (RSAPrivateKey) key;
            return this;
        }

        @Override
        public RsaEncryptedJwtDecoder build() throws MissingDecodingKeyException {
            if (key == null) {
                throw new MissingDecodingKeyException();
            }
            return new RsaEncryptedJwtDecoder(key);
        }

        private static void validateKeyType(Key key)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            if (!(key instanceof RSAPrivateKey)) {
                throw new InvalidDecodingKeyTypeException(key.getClass(),
                    EncryptedJwtAlgorithm.SPEC_RSA_SUPPORTED_ALGORITHMS);
            }
            try {
                new RSADecrypter((PrivateKey) key);
            } catch (IllegalArgumentException e) {
                throw new InvalidDecodingKeyAlgorithmException(key.getAlgorithm(),
                    EncryptedJwtAlgorithm.SPEC_RSA_SUPPORTED_ALGORITHMS, e);
            }
        }
    }
}
