package com.extole.common.jwt.encode.encrypt;

import java.security.Key;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.EncryptionMethod;
import com.extole.common.jwt.JwtRuntimeException;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.InvalidEncodingKeyAlgorithmException;
import com.extole.common.jwt.encode.InvalidEncodingKeyTypeException;
import com.extole.common.jwt.encode.MissingEncodingAlgorithmException;
import com.extole.common.jwt.encode.MissingEncodingKeyException;

final class RsaEncryptedJwtEncoder implements EncryptedJwtEncoder {

    private final Algorithm algorithm;
    private final EncryptionMethod encryptionMethod;
    private final RSAPublicKey publicKey;

    private RsaEncryptedJwtEncoder(Algorithm algorithm,
        EncryptionMethod encryptionMethod,
        RSAPublicKey publicKey) {
        this.algorithm = algorithm;
        this.encryptionMethod = encryptionMethod;
        this.publicKey = publicKey;
    }

    @Override
    public String toEncodedString(Map<String, Object> headers, Map<String, Object> claims) {
        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.parse(algorithm.getSpecName()),
            com.nimbusds.jose.EncryptionMethod.parse(encryptionMethod.getSpecName()))
                .customParams(Map.copyOf(headers))
                .build();

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
        claims.forEach(jwtClaimsSetBuilder::claim);

        EncryptedJWT encryptedJWT = new EncryptedJWT(header, jwtClaimsSetBuilder.build());
        JWEEncrypter encrypter = new RSAEncrypter(publicKey);

        try {
            encryptedJWT.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new JwtRuntimeException("Could not encrypt jwt using algorithm: " + algorithm.getName(), e);
        }

        return encryptedJWT.serialize();
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements EncryptedJwtEncoderBuilder {

        private Algorithm algorithm;
        private EncryptionMethod encryptionMethod;
        private RSAPublicKey key;

        private Builder() {
        }

        @Override
        public Builder withAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            validateAlgorithm(algorithm);
            this.algorithm = algorithm;
            return this;
        }

        @Override
        public Builder withEncryptionMethod(EncryptionMethod encryptionMethod)
            throws UnsupportedEncryptionMethodException {
            validateEncryptionMethod(encryptionMethod);
            this.encryptionMethod = encryptionMethod;
            return this;
        }

        @Override
        public Builder withKey(Key key) throws InvalidEncodingKeyTypeException {
            validateKeyType(key);
            this.key = (RSAPublicKey) key;
            return this;
        }

        @Override
        public RsaEncryptedJwtEncoder build() throws MissingEncodingAlgorithmException,
            MissingEncodingKeyException, MissingEncryptionMethodException, InvalidEncodingKeyAlgorithmException {
            validateBuild();
            return new RsaEncryptedJwtEncoder(algorithm, encryptionMethod, key);
        }

        private void validateBuild() throws MissingEncodingAlgorithmException,
            MissingEncodingKeyException, MissingEncryptionMethodException, InvalidEncodingKeyAlgorithmException {
            if (encryptionMethod == null) {
                throw new MissingEncryptionMethodException();
            }
            if (algorithm == null) {
                throw new MissingEncodingAlgorithmException();
            }
            if (key == null) {
                throw new MissingEncodingKeyException();
            }
            validateKeyAlgorithm(algorithm, encryptionMethod, key);
        }

        private static void validateKeyAlgorithm(Algorithm algorithm, EncryptionMethod encryptionMethod,
            RSAPublicKey key) throws InvalidEncodingKeyAlgorithmException {
            EncryptedJWT internalValidationJwe = InternalValidationJweSupplier.get(algorithm, encryptionMethod);
            JWEEncrypter encrypter = new RSAEncrypter(key);
            try {
                internalValidationJwe.encrypt(encrypter);
            } catch (JOSEException e) {
                throw new InvalidEncodingKeyAlgorithmException(key.getAlgorithm(), algorithm, e);
            }
        }

        private static void validateAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            if (!EncryptedJwtAlgorithm.RSA_SUPPORTED_ALGORITHMS.contains(algorithm)) {
                throw new UnsupportedAlgorithmException(algorithm.getName(),
                    EncryptedJwtAlgorithm.RSA_SUPPORTED_ALGORITHMS);
            }
        }

        private static void validateEncryptionMethod(EncryptionMethod encryptionMethod)
            throws UnsupportedEncryptionMethodException {
            if (!EncryptionMethod.SUPPORTED_ENCRYPTION_METHODS.contains(encryptionMethod)) {
                throw new UnsupportedEncryptionMethodException(encryptionMethod,
                    EncryptionMethod.SUPPORTED_ENCRYPTION_METHODS);
            }
        }

        private static void validateKeyType(Key key) throws InvalidEncodingKeyTypeException {
            if (!(key instanceof RSAPublicKey)) {
                throw new InvalidEncodingKeyTypeException(key.getClass(),
                    EncryptedJwtAlgorithm.RSA_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
