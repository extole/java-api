package com.extole.common.jwt.encode;

import java.security.Key;
import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.InvalidKeyTypeException;

public class InvalidEncodingKeyTypeException extends InvalidKeyTypeException {

    private static final String MESSAGE_PATTERN =
        "Key of type: %s cannot be used to create encoded JWT with any algorithm: %s";

    public InvalidEncodingKeyTypeException(Class<? extends Key> keyClass, Set<Algorithm> supportedAlgorithms) {
        super(String.format(MESSAGE_PATTERN, keyClass, supportedAlgorithms), keyClass, supportedAlgorithms);
    }
}
