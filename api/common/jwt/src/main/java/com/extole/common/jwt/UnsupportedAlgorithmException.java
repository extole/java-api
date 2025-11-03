package com.extole.common.jwt;

import java.util.Set;

public class UnsupportedAlgorithmException extends JwtException {

    private static final String MESSAGE_PATTERN = "Algorithm %s is not supported by JWT library";

    private final String algorithm;
    private final Set<Algorithm> supportedAlgorithms;

    public UnsupportedAlgorithmException(String algorithm, Set<Algorithm> supportedAlgorithms) {
        super(String.format(MESSAGE_PATTERN, algorithm));
        this.algorithm = algorithm;
        this.supportedAlgorithms = Set.copyOf(supportedAlgorithms);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Set<Algorithm> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }
}
