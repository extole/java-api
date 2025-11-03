package com.extole.common.jwt.decode.sign;

import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InvalidDecodingKeyTypeException;
import com.extole.common.jwt.decode.InvalidSecuredJwtDecoderException;
import com.extole.common.jwt.decode.MissingDecodingKeyException;

final class RsaSignedJwtDecoder implements InternalSecuredJwtDecoder<SignedUncheckedJwt> {

    private final RSAPublicKey key;

    private RsaSignedJwtDecoder(RSAPublicKey key) {
        this.key = key;
    }

    @Override
    public SignedJwt toDecodedObject(SignedUncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        JWSVerifier verifier = new RSASSAVerifier(key);
        try {
            SignedJWT signedJWT = SignedJWT.parse(uncheckedJwt.getEncodedJwtString());
            boolean verified = signedJWT.verify(verifier);

            if (!verified) {
                throw new InvalidSecuredJwtDecoderException("JWT is not signed with verification key");
            }
            return new SignedJwt(uncheckedJwt.getHeader(), signedJWT.getPayload().toJSONObject());
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

        private RSAPublicKey key;

        private Builder() {
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException {
            validateKey(key);
            this.key = (RSAPublicKey) key;
            return this;
        }

        @Override
        public RsaSignedJwtDecoder build() throws MissingDecodingKeyException {
            if (key == null) {
                throw new MissingDecodingKeyException();
            }
            return new RsaSignedJwtDecoder(key);
        }

        private static void validateKey(Key key) throws InvalidDecodingKeyTypeException {
            if (!(key instanceof RSAPublicKey)) {
                throw new InvalidDecodingKeyTypeException(key.getClass(),
                    SignedJwtAlgorithm.SPEC_RS_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
