package com.extole.common.jwt.decode;

import java.security.Key;
import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.InvalidKeyTypeException;

public class InvalidDecodingKeyTypeException extends InvalidKeyTypeException {

    private static final String MESSAGE_PATTERN =
        "Key of type: %s cannot be used to decode JWT that is encoded with any algorithms of: %s";

    public InvalidDecodingKeyTypeException(Class<? extends Key> keyClass, Set<Algorithm> supportedAlgorithms) {
        super(String.format(MESSAGE_PATTERN, keyClass.getName(), supportedAlgorithms), keyClass, supportedAlgorithms);
    }
}
