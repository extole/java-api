package com.extole.common.jwt;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class SignedJwtAlgorithm {

    public static final Algorithm HS256 = new Algorithm("HS256", "HmacSHA256", 256, true);
    public static final Algorithm HS384 = new Algorithm("HS384", "HmacSHA384", 384, true);
    public static final Algorithm HS512 = new Algorithm("HS512", "HmacSHA512", 512, true);

    public static final Algorithm ES256 = new Algorithm("ES256", "EC", 256, false);
    public static final Algorithm ES384 = new Algorithm("ES384", "EC", 384, false);
    public static final Algorithm ES512 = new Algorithm("ES512", "EC", 521, false);
    public static final Algorithm ES256_PUBLIC = new Algorithm("ES256_PUBLIC", "ES256", "EC", 256, false);
    public static final Algorithm ES384_PUBLIC = new Algorithm("ES384_PUBLIC", "ES384", "EC", 384, false);
    public static final Algorithm ES512_PUBLIC = new Algorithm("ES512_PUBLIC", "ES512", "EC", 521, false);
    public static final Algorithm ES256_PRIVATE = new Algorithm("ES256_PRIVATE", "ES256", "EC", 256, false);
    public static final Algorithm ES384_PRIVATE = new Algorithm("ES384_PRIVATE", "ES384", "EC", 384, false);
    public static final Algorithm ES512_PRIVATE = new Algorithm("ES512_PRIVATE", "ES512", "EC", 521, false);

    public static final Algorithm RS256 = new Algorithm("RS256", "RSA", 2048, false);
    public static final Algorithm RS384 = new Algorithm("RS384", "RSA", 2048, false);
    public static final Algorithm RS512 = new Algorithm("RS512", "RSA", 2048, false);
    public static final Algorithm RS256_PUBLIC = new Algorithm("RS256_PUBLIC", "RS256", "RSA", 2048, false);
    public static final Algorithm RS384_PUBLIC = new Algorithm("RS384_PUBLIC", "RS384", "RSA", 2048, false);
    public static final Algorithm RS512_PUBLIC = new Algorithm("RS512_PUBLIC", "RS512", "RSA", 2048, false);
    public static final Algorithm RS256_PRIVATE = new Algorithm("RS256_PRIVATE", "RS256", "RSA", 2048, false);
    public static final Algorithm RS384_PRIVATE = new Algorithm("RS384_PRIVATE", "RS384", "RSA", 2048, false);
    public static final Algorithm RS512_PRIVATE = new Algorithm("RS512_PRIVATE", "RS512", "RSA", 2048, false);

    public static final Algorithm PS256 = new Algorithm("PS256", "RSA", 2048, false);
    public static final Algorithm PS384 = new Algorithm("PS384", "RSA", 2048, false);
    public static final Algorithm PS512 = new Algorithm("PS512", "RSA", 2048, false);
    public static final Algorithm PS256_PUBLIC = new Algorithm("PS256_PUBLIC", "PS256", "RSA", 2048, false);
    public static final Algorithm PS384_PUBLIC = new Algorithm("PS384_PUBLIC", "PS384", "RSA", 2048, false);
    public static final Algorithm PS512_PUBLIC = new Algorithm("PS512_PUBLIC", "PS512", "RSA", 2048, false);
    public static final Algorithm PS256_PRIVATE = new Algorithm("PS256_PRIVATE", "PS256", "RSA", 2048, false);
    public static final Algorithm PS384_PRIVATE = new Algorithm("PS384_PRIVATE", "PS384", "RSA", 2048, false);
    public static final Algorithm PS512_PRIVATE = new Algorithm("PS512_PRIVATE", "PS512", "RSA", 2048, false);

    public static final Set<Algorithm> HS_SUPPORTED_ALGORITHMS = Set.of(HS256, HS384, HS512);

    public static final Set<Algorithm> SPEC_ES_SUPPORTED_ALGORITHMS = Set.of(ES256, ES384, ES512);
    public static final Set<Algorithm> PUBLIC_ES_SUPPORTED_ALGORITHMS =
        Set.of(ES256_PUBLIC, ES384_PUBLIC, ES512_PUBLIC);
    public static final Set<Algorithm> PRIVATE_ES_SUPPORTED_ALGORITHMS =
        Set.of(ES256_PRIVATE, ES384_PRIVATE, ES512_PRIVATE);
    public static final Set<Algorithm> ES_SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(SPEC_ES_SUPPORTED_ALGORITHMS)
        .addAll(PUBLIC_ES_SUPPORTED_ALGORITHMS)
        .addAll(PRIVATE_ES_SUPPORTED_ALGORITHMS)
        .build();

    public static final Set<Algorithm> SPEC_RS_SUPPORTED_ALGORITHMS = Set.of(RS256, RS384, RS512, PS256, PS384, PS512);
    public static final Set<Algorithm> PUBLIC_RS_SUPPORTED_ALGORITHMS =
        Set.of(RS256_PUBLIC, RS384_PUBLIC, RS512_PUBLIC, PS256_PUBLIC, PS384_PUBLIC, PS512_PUBLIC);
    public static final Set<Algorithm> PRIVATE_RS_SUPPORTED_ALGORITHMS =
        Set.of(RS256_PRIVATE, RS384_PRIVATE, RS512_PRIVATE, PS256_PRIVATE, PS384_PRIVATE, PS512_PRIVATE);
    public static final Set<Algorithm> RS_SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(SPEC_RS_SUPPORTED_ALGORITHMS)
        .addAll(PUBLIC_RS_SUPPORTED_ALGORITHMS)
        .addAll(PRIVATE_RS_SUPPORTED_ALGORITHMS)
        .build();

    public static final Set<Algorithm> SPEC_SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(HS_SUPPORTED_ALGORITHMS)
        .addAll(SPEC_ES_SUPPORTED_ALGORITHMS)
        .addAll(SPEC_RS_SUPPORTED_ALGORITHMS)
        .build();
    public static final Set<Algorithm> SUPPORTED_ALGORITHMS = ImmutableSet.<Algorithm>builder()
        .addAll(HS_SUPPORTED_ALGORITHMS)
        .addAll(ES_SUPPORTED_ALGORITHMS)
        .addAll(RS_SUPPORTED_ALGORITHMS)
        .build();

    private SignedJwtAlgorithm() {
    }

    public static Algorithm parse(String algorithmName) throws UnsupportedAlgorithmException {
        return Algorithm.parse(algorithmName, algorithm -> algorithm.getName(), SUPPORTED_ALGORITHMS);
    }

    public static Algorithm parseBySpecName(String specAlgorithmName) throws UnsupportedAlgorithmException {
        return Algorithm.parse(specAlgorithmName, algorithm -> algorithm.getSpecName(), SPEC_SUPPORTED_ALGORITHMS);
    }
}
