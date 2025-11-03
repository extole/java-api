package com.extole.common.jwt;

import java.security.Key;
import java.util.Set;

public abstract class InvalidKeyTypeException extends InvalidKeyException {

    private final Class<? extends Key> keyClass;
    private final Set<Algorithm> supportedAlgorithms;

    public InvalidKeyTypeException(String message, Class<? extends Key> keyClass, Set<Algorithm> supportedAlgorithms) {
        super(message);
        this.keyClass = keyClass;
        this.supportedAlgorithms = Set.copyOf(supportedAlgorithms);
    }

    public Class<? extends Key> getKeyClass() {
        return keyClass;
    }

    public Set<Algorithm> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }
}
