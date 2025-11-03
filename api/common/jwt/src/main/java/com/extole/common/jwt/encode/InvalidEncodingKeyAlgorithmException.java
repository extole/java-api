package com.extole.common.jwt.encode;

import com.nimbusds.jose.JOSEException;

import com.extole.common.jwt.Algorithm;

public class InvalidEncodingKeyAlgorithmException extends JwtEncoderBuildException {

    private static final String MESSAGE_PATTERN = "Key of algorithm %s cannot be used to encode JWT with algorithm: %s";

    public InvalidEncodingKeyAlgorithmException(String keyAlgorithm, Algorithm algorithm, JOSEException e) {
        super(String.format(MESSAGE_PATTERN, keyAlgorithm, algorithm.getName()), e);
    }
}
