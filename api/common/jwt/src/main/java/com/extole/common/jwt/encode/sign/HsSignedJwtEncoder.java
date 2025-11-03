package com.extole.common.jwt.encode.sign;

import java.security.Key;
import java.util.Map;

import javax.crypto.SecretKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
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

final class HsSignedJwtEncoder implements SecuredJwtEncoder {

    private final Algorithm algorithm;
    private final SecretKey key;

    private HsSignedJwtEncoder(Algorithm algorithm, SecretKey key) {
        this.algorithm = algorithm;
        this.key = key;
    }

    @Override
    public String toEncodedString(Map<String, Object> headers, Map<String, Object> claims) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(algorithm.getSpecName()))
            .customParams(Map.copyOf(headers))
            .build();

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
        claims.forEach(jwtClaimsSetBuilder::claim);

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSetBuilder.build());
        try {
            JWSSigner signer = new MACSigner(key);
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

        private Algorithm algorithm;
        private SecretKey key;

        private Builder() {
        }

        @Override
        public Builder withAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            validateAlgorithm(algorithm);
            this.algorithm = algorithm;
            return this;
        }

        @Override
        public Builder withKey(Key key) throws InvalidEncodingKeyTypeException {
            validateKeyType(key);
            this.key = (SecretKey) key;
            return this;
        }

        @Override
        public HsSignedJwtEncoder build() throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            validateBuild();
            return new HsSignedJwtEncoder(algorithm, key);
        }

        private void validateBuild() throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            if (algorithm == null) {
                throw new MissingEncodingAlgorithmException();
            }
            if (key == null) {
                throw new MissingEncodingKeyException();
            }
            validateKeyAlgorithm(algorithm, key);
        }

        private static void validateKeyAlgorithm(Algorithm algorithm, SecretKey key)
            throws InvalidEncodingKeyAlgorithmException {
            SignedJWT internalValidationJws = InternalValidationJwsSupplier.get(algorithm);
            try {
                JWSSigner signer = new MACSigner(key);
                internalValidationJws.sign(signer);
            } catch (JOSEException e) {
                throw new InvalidEncodingKeyAlgorithmException(key.getAlgorithm(), algorithm, e);
            }
        }

        private static void validateAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            if (!SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS.contains(algorithm)) {
                throw new UnsupportedAlgorithmException(algorithm.getName(),
                    SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS);
            }
        }

        private static void validateKeyType(Key key) throws InvalidEncodingKeyTypeException {
            if (!(key instanceof SecretKey)) {
                throw new InvalidEncodingKeyTypeException(key.getClass(), SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
