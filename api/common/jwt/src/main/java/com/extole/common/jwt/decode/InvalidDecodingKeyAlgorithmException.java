package com.extole.common.jwt.decode;

import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.InvalidKeyException;

public class InvalidDecodingKeyAlgorithmException extends InvalidKeyException {

    private static final String MESSAGE_PATTERN =
        "Key of algorithm: %s cannot be used to decode JWT encoded with any of %s";

    public InvalidDecodingKeyAlgorithmException(String algorithm, Set<Algorithm> supportedAlgorithms, Throwable cause) {
        super(String.format(MESSAGE_PATTERN, algorithm, supportedAlgorithms), cause);
    }
}
