package com.extole.common.jwt.keygen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.extole.common.jwt.Algorithm;

public final class Keys {

    private Keys() {
    }

    public static KeyPair keyPairFor(Algorithm algorithm) throws IllegalArgumentException {
        return keyPairFor(algorithm, algorithm.getSize());
    }

    public static KeyPair keyPairFor(Algorithm algorithm, int size) {
        return keyPairFor(algorithm, size, null);
    }

    public static KeyPair keyPairFor(Algorithm algorithm, Provider provider) {
        return keyPairFor(algorithm, algorithm.getSize(), provider);
    }

    public static KeyPair keyPairFor(Algorithm algorithm, int size, Provider provider) {
        if (algorithm.isSymmetric()) {
            throw new IllegalArgumentException(
                "The algorithm: " + algorithm.getName() + " is not asymmetric and cannot be used for creating KeyPair");
        }
        try {
            KeyPairGenerator keyGenerator =
                provider != null ? KeyPairGenerator.getInstance(algorithm.getJcaName(), provider)
                    : KeyPairGenerator.getInstance(algorithm.getJcaName());
            keyGenerator.initialize(size);
            return keyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to obtain a KeyPairGenerator for algorithm: " + algorithm, e);
        }
    }

    public static SecretKey secretKeyFor(Algorithm algorithm) {
        return secretKeyFor(algorithm, algorithm.getSize());
    }

    public static SecretKey secretKeyFor(Algorithm algorithm, int size) {
        if (!algorithm.isSymmetric()) {
            throw new IllegalArgumentException("The algorithm: " + algorithm.getName()
                + " is not symmetric and cannot be used for creating SecretKey");
        }
        try {
            KeyGenerator generator = KeyGenerator.getInstance(algorithm.getJcaName());
            generator.init(size);
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to obtain a KeyGenerator for algorithm: " + algorithm, e);
        }
    }
}
