package com.extole.common.jwt.encode.sign;

import java.security.Key;
import java.security.interfaces.ECPrivateKey;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
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

final class EsSignedJwtEncoder implements SecuredJwtEncoder {

    private final Algorithm algorithm;
    private final ECPrivateKey ecPrivateKey;

    private EsSignedJwtEncoder(Algorithm algorithm, ECPrivateKey ecPrivateKey) {
        this.algorithm = algorithm;
        this.ecPrivateKey = ecPrivateKey;
    }

    @Override
    public String toEncodedString(Map<String, Object> headers, Map<String, Object> claims) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(algorithm.getSpecName()))
            .customParams(Map.copyOf(headers))
            .build();

        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        claims.forEach(claimsSetBuilder::claim);

        SignedJWT signedJWT = new SignedJWT(header, claimsSetBuilder.build());
        try {
            JWSSigner signer = new ECDSASigner(ecPrivateKey);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new JwtRuntimeException("Could not sign JWT with algorithm:" + algorithm.getName(), e);
        }

        return signedJWT.serialize();
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements SecuredJwtEncoderBuilder {

        private Algorithm algorithm;
        private ECPrivateKey ecPrivateKey;

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
            this.ecPrivateKey = (ECPrivateKey) key;
            return this;
        }

        @Override
        public EsSignedJwtEncoder build() throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            validateBuild();
            return new EsSignedJwtEncoder(algorithm, ecPrivateKey);
        }

        private void validateBuild()
            throws MissingEncodingAlgorithmException, MissingEncodingKeyException,
            InvalidEncodingKeyAlgorithmException {
            if (algorithm == null) {
                throw new MissingEncodingAlgorithmException();
            }
            if (ecPrivateKey == null) {
                throw new MissingEncodingKeyException();
            }
            validateKeyAlgorithm(algorithm, ecPrivateKey);
        }

        private static void validateKeyAlgorithm(Algorithm algorithm, ECPrivateKey ecPrivateKey)
            throws InvalidEncodingKeyAlgorithmException {
            SignedJWT internalValidationJws = InternalValidationJwsSupplier.get(algorithm);
            try {
                JWSSigner signer = new ECDSASigner(ecPrivateKey);
                internalValidationJws.sign(signer);
            } catch (JOSEException e) {
                throw new InvalidEncodingKeyAlgorithmException(ecPrivateKey.getAlgorithm(), algorithm, e);
            }
        }

        private static void validateAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            if (!SignedJwtAlgorithm.ES_SUPPORTED_ALGORITHMS.contains(algorithm)) {
                throw new UnsupportedAlgorithmException(algorithm.getName(),
                    SignedJwtAlgorithm.ES_SUPPORTED_ALGORITHMS);
            }
        }

        private static void validateKeyType(Key key) throws InvalidEncodingKeyTypeException {
            if (!(key instanceof ECPrivateKey)) {
                throw new InvalidEncodingKeyTypeException(key.getClass(), SignedJwtAlgorithm.ES_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
