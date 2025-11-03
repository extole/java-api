package com.extole.common.jwt.decode.sign;

import java.security.Key;
import java.text.ParseException;

import javax.crypto.SecretKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InvalidDecodingKeyAlgorithmException;
import com.extole.common.jwt.decode.InvalidDecodingKeyTypeException;
import com.extole.common.jwt.decode.InvalidSecuredJwtDecoderException;
import com.extole.common.jwt.decode.MissingDecodingKeyException;

final class HsSignedJwtDecoder implements InternalSecuredJwtDecoder<SignedUncheckedJwt> {

    private final SecretKey secretKey;

    private HsSignedJwtDecoder(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public SignedJwt toDecodedObject(SignedUncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        try {
            SignedJWT internalJwt = SignedJWT.parse(uncheckedJwt.getEncodedJwtString());
            JWSVerifier verifier = new MACVerifier(secretKey);
            boolean verified = internalJwt.verify(verifier);
            if (!verified) {
                throw new InvalidSecuredJwtDecoderException("JWT is not signed with verification key");
            }
            return new SignedJwt(uncheckedJwt.getHeader(), internalJwt.getPayload().toJSONObject());
        } catch (ParseException e) {
            throw new JwtRuntimeException("Could not parse encoded JWT", e);
        } catch (JOSEException e) {
            throw new InvalidSecuredJwtDecoderException("JWT signature verification failed", e);
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements InternalSecuredJwtDecoderBuilder<SignedUncheckedJwt> {

        private SecretKey secretKey;

        private Builder() {
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            validateKeyType(key);
            this.secretKey = (SecretKey) key;
            return this;
        }

        @Override
        public HsSignedJwtDecoder build() throws MissingDecodingKeyException {
            if (secretKey == null) {
                throw new MissingDecodingKeyException();
            }
            return new HsSignedJwtDecoder(secretKey);
        }

        private static void validateKeyType(Key key)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            if (!(key instanceof SecretKey)) {
                throw new InvalidDecodingKeyTypeException(key.getClass(), SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS);
            }
            try {
                new MACVerifier((SecretKey) key);
            } catch (JOSEException e) {
                throw new InvalidDecodingKeyAlgorithmException(key.getAlgorithm(),
                    SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS, e);
            }

        }
    }
}
