package com.extole.common.jwt;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class EncryptedJwtAlgorithm {

    public static final Algorithm A128KW = new Algorithm("A128KW", "AES", 128, true);
    public static final Algorithm A192KW = new Algorithm("A192KW", "AES", 192, true);
    public static final Algorithm A256KW = new Algorithm("A256KW", "AES", 256, true);

    public static final Algorithm RSA_OAEP_256 = new Algorithm("RSA_OAEP_256", "RSA-OAEP-256", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_384 = new Algorithm("RSA_OAEP_384", "RSA-OAEP-384", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_512 = new Algorithm("RSA_OAEP_512", "RSA-OAEP-512", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_256_PUBLIC =
        new Algorithm("RSA_OAEP_256_PUBLIC", "RSA-OAEP-256", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_384_PUBLIC =
        new Algorithm("RSA_OAEP_384_PUBLIC", "RSA-OAEP-384", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_512_PUBLIC =
        new Algorithm("RSA_OAEP_512_PUBLIC", "RSA-OAEP-512", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_256_PRIVATE =
        new Algorithm("RSA_OAEP_256_PRIVATE", "RSA-OAEP-256", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_384_PRIVATE =
        new Algorithm("RSA_OAEP_384_PRIVATE", "RSA-OAEP-384", "RSA", 2048, false);
    public static final Algorithm RSA_OAEP_512_PRIVATE =
        new Algorithm("RSA_OAEP_512_PRIVATE", "RSA-OAEP-512", "RSA", 2048, false);

    public static final Set<Algorithm> SPEC_RSA_SUPPORTED_ALGORITHMS = Set.of(RSA_OAEP_256, RSA_OAEP_384, RSA_OAEP_512);
    public static final Set<Algorithm> PUBLIC_RSA_SUPPORTED_ALGORITHMS =
        Set.of(RSA_OAEP_256_PUBLIC, RSA_OAEP_384_PUBLIC, RSA_OAEP_512_PUBLIC);
    public static final Set<Algorithm> PRIVATE_RSA_SUPPORTED_ALGORITHMS =
        Set.of(RSA_OAEP_256_PRIVATE, RSA_OAEP_384_PRIVATE, RSA_OAEP_512_PRIVATE);
    public static final Set<Algorithm> RSA_SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(SPEC_RSA_SUPPORTED_ALGORITHMS)
        .addAll(PUBLIC_RSA_SUPPORTED_ALGORITHMS)
        .addAll(PRIVATE_RSA_SUPPORTED_ALGORITHMS)
        .build();

    public static final Set<Algorithm> AES_SUPPORTED_ALGORITHMS = Set.of(A128KW, A192KW, A256KW);

    public static final Set<Algorithm> SPEC_SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(SPEC_RSA_SUPPORTED_ALGORITHMS)
        .addAll(AES_SUPPORTED_ALGORITHMS)
        .build();
    public static final Set<Algorithm> SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(RSA_SUPPORTED_ALGORITHMS)
        .addAll(AES_SUPPORTED_ALGORITHMS)
        .build();

    private EncryptedJwtAlgorithm() {
    }

    public static Algorithm parse(String algorithmName) throws UnsupportedAlgorithmException {
        return Algorithm.parse(algorithmName, algorithm -> algorithm.getName(), SUPPORTED_ALGORITHMS);
    }

    public static Algorithm parseBySpecName(String specAlgorithmName) throws UnsupportedAlgorithmException {
        return Algorithm.parse(specAlgorithmName, algorithm -> algorithm.getSpecName(), SPEC_SUPPORTED_ALGORITHMS);
    }
}
