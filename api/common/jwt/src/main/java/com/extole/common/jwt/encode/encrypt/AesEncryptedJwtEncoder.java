package com.extole.common.jwt.encode.encrypt;

import java.security.Key;
import java.util.Map;

import javax.crypto.SecretKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.AESEncrypter;
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

final class AesEncryptedJwtEncoder implements EncryptedJwtEncoder {

    private final Algorithm algorithm;
    private final EncryptionMethod encryptionMethod;
    private final SecretKey key;

    private AesEncryptedJwtEncoder(Algorithm algorithm,
        EncryptionMethod encryptionMethod,
        SecretKey key) {
        this.algorithm = algorithm;
        this.encryptionMethod = encryptionMethod;
        this.key = key;
    }

    @Override
    public String toEncodedString(Map<String, Object> headers, Map<String, Object> claims) {
        JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.parse(algorithm.getSpecName()),
            com.nimbusds.jose.EncryptionMethod.parse(encryptionMethod.getSpecName())).customParams(headers).build();

        JWTClaimsSet.Builder jwtClaimsBuilder = new JWTClaimsSet.Builder();
        claims.forEach(jwtClaimsBuilder::claim);

        EncryptedJWT jwt = new EncryptedJWT(header, jwtClaimsBuilder.build());
        try {
            JWEEncrypter encrypter = new AESEncrypter(key);
            jwt.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new JwtRuntimeException("Could not encrypt jwt using algorithm: " + algorithm.getName(), e);
        }

        return jwt.serialize();
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder implements EncryptedJwtEncoderBuilder {

        private EncryptionMethod encryptionMethod;
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
        public Builder withEncryptionMethod(EncryptionMethod encryptionMethod)
            throws UnsupportedEncryptionMethodException {
            validateEncryptionMethod(encryptionMethod);
            this.encryptionMethod = encryptionMethod;
            return this;
        }

        @Override
        public Builder withKey(Key key) throws InvalidEncodingKeyTypeException {
            validateKeyType(key);
            this.key = (SecretKey) key;
            return this;
        }

        @Override
        public AesEncryptedJwtEncoder build() throws MissingEncryptionMethodException,
            MissingEncodingKeyException, MissingEncodingAlgorithmException, InvalidEncodingKeyAlgorithmException {
            validateBuild();
            return new AesEncryptedJwtEncoder(algorithm, encryptionMethod, key);
        }

        private void validateBuild() throws MissingEncryptionMethodException, MissingEncodingAlgorithmException,
            MissingEncodingKeyException, InvalidEncodingKeyAlgorithmException {
            if (algorithm == null) {
                throw new MissingEncodingAlgorithmException();
            }
            if (encryptionMethod == null) {
                throw new MissingEncryptionMethodException();
            }
            if (key == null) {
                throw new MissingEncodingKeyException();
            }
            validateKeyAlgorithm(algorithm, encryptionMethod, key);
        }

        private static void validateKeyAlgorithm(Algorithm algorithm, EncryptionMethod encryptionMethod,
            SecretKey key) throws InvalidEncodingKeyAlgorithmException {
            EncryptedJWT encryptedJWT = InternalValidationJweSupplier.get(algorithm, encryptionMethod);
            try {
                JWEEncrypter encrypter = new AESEncrypter(key);
                encryptedJWT.encrypt(encrypter);
            } catch (JOSEException e) {
                throw new InvalidEncodingKeyAlgorithmException(key.getAlgorithm(), algorithm, e);
            }
        }

        private static void validateAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException {
            if (!EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS.contains(algorithm)) {
                throw new UnsupportedAlgorithmException(algorithm.getSpecName(),
                    EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS);
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
            if (!(key instanceof SecretKey)) {
                throw new InvalidEncodingKeyTypeException(key.getClass(),
                    EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS);
            }
        }
    }
}
