package com.extole.common.jwt.decode.sign;

import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InvalidDecodingKeyAlgorithmException;
import com.extole.common.jwt.decode.InvalidDecodingKeyTypeException;
import com.extole.common.jwt.decode.InvalidSecuredJwtDecoderException;
import com.extole.common.jwt.decode.MissingDecodingKeyException;

final class EsSignedJwtDecoder implements InternalSecuredJwtDecoder<SignedUncheckedJwt> {

    private final ECPublicKey publicKey;

    private EsSignedJwtDecoder(ECPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public SignedJwt toDecodedObject(SignedUncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        try {
            SignedJWT internalDecodedJwt = SignedJWT.parse(uncheckedJwt.getEncodedJwtString());
            JWSVerifier verifier = createJwsVerifier(publicKey);
            if (!internalDecodedJwt.verify(verifier)) {
                throw new InvalidSecuredJwtDecoderException("JWT is not signed with verification key");
            }
            return new SignedJwt(uncheckedJwt.getHeader(), internalDecodedJwt.getPayload().toJSONObject());
        } catch (ParseException e) {
            throw new JwtRuntimeException("Could not parse encoded JWT", e);
        } catch (JOSEException e) {
            throw new InvalidSecuredJwtDecoderException("JWT signature verification failed", e);
        }
    }

    private JWSVerifier createJwsVerifier(ECPublicKey publicKey) {
        try {
            return new ECDSAVerifier(publicKey);
        } catch (JOSEException e) {
            throw new JwtRuntimeException("JWT signature verification failed", e);
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements InternalSecuredJwtDecoderBuilder<SignedUncheckedJwt> {

        private ECPublicKey publicKey;

        private Builder() {
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            validateKey(key);
            this.publicKey = (ECPublicKey) key;
            return this;
        }

        @Override
        public EsSignedJwtDecoder build() throws MissingDecodingKeyException {
            if (publicKey == null) {
                throw new MissingDecodingKeyException();
            }
            return new EsSignedJwtDecoder(publicKey);
        }

        private static void validateKey(Key key)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            if (!(key instanceof ECPublicKey)) {
                throw new InvalidDecodingKeyTypeException(key.getClass(),
                    SignedJwtAlgorithm.SPEC_SUPPORTED_ALGORITHMS);
            }
            try {
                new ECDSAVerifier((ECPublicKey) key);
            } catch (JOSEException e) {
                throw new InvalidDecodingKeyAlgorithmException(key.getAlgorithm(),
                    SignedJwtAlgorithm.SPEC_SUPPORTED_ALGORITHMS, e);
            }
        }
    }
}
