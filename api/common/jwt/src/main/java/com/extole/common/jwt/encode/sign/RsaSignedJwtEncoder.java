package com.extole.common.jwt.encode.sign;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.InvalidEncodingKeyAlgorithmException;
import com.extole.common.jwt.encode.InvalidEncodingKeyTypeException;
import com.extole.common.jwt.encode.MissingEncodingAlgorithmException;
import com.extole.common.jwt.encode.MissingEncodingKeyException;
import com.extole.common.jwt.encode.SecuredJwtEncoder;

final class RsaSignedJwtEncoder implements SecuredJwtEncoder {

    private final Algorithm algorithm;
    private final RSAPrivateKey key;

    private RsaSignedJwtEncoder(Algorithm algorithm, RSAPrivateKey key) {
        this.algorithm = algorithm;
        this.key = key;
    }

    @Override
    public String toEncodedString(Map<String, Object> headers, Map<String, Object> claims) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(algorithm.getSpecName()))
            .customParams(headers)
            .build();
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        claims.forEach(claimsSetBuilder::claim);

        SignedJWT signedJWT = new SignedJWT(header, claimsSetBuilder.build());
        try {
            JWSSigner signer = new RSASSASigner(key);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new JwtRuntimeException("Could not sign JWT with algorithm: " + algorithm.getName(), e);
        }
        return signedJWT.serialize();
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements SecuredJwtEncoderBuilder {

        private Builder() {
        }

        private Algorithm algorithm;
        private RSAPrivateKey key;

        @Override
        public Builder withAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            validateAlgorithm(algorithm);
            this.algorithm = algorithm;
            return this;
        }

        @Override
        public Builder withKey(Key key) throws InvalidEncodingKeyTypeException {
            validateKeyType(key);
            this.key = (RSAPrivateKey) key;
            return this;
        }

        @Override
        public RsaSignedJwtEncoder build() throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            validateRequiredFieldsAreSet();
            return new RsaSignedJwtEncoder(algorithm, key);
        }

        private void validateRequiredFieldsAreSet()
            throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            if (algorithm == null) {
                throw new MissingEncodingAlgorithmException();
            }
            if (key == null) {
                throw new MissingEncodingKeyException();
            }
            validateKeyAlgorithm(algorithm, key);
        }

        private void validateKeyAlgorithm(Algorithm algorithm, RSAPrivateKey key)
            throws InvalidEncodingKeyAlgorithmException {
            SignedJWT internalValidationJws = InternalValidationJwsSupplier.get(SignedJwtAlgorithm.PS256);
            JWSSigner signer = new RSASSASigner(key);
            try {
                internalValidationJws.sign(signer);
            } catch (JOSEException e) {
                throw new InvalidEncodingKeyAlgorithmException(key.getAlgorithm(), algorithm, e);
            }
        }

        private static void validateAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            if (!SignedJwtAlgorithm.RS_SUPPORTED_ALGORITHMS.contains(algorithm)) {
                throw new UnsupportedAlgorithmException(algorithm.getName(),
                    SignedJwtAlgorithm.RS_SUPPORTED_ALGORITHMS);
            }
        }

        private static void validateKeyType(Key key) throws InvalidEncodingKeyTypeException {
            if (!(key instanceof RSAPrivateKey)) {
                throw new InvalidEncodingKeyTypeException(key.getClass(), SignedJwtAlgorithm.RS_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
