package com.extole.common.jwt.encode;

public class MissingEncodingAlgorithmException extends JwtEncoderBuildException {

    private static final String MESSAGE = "Cannot create encoder without algorithm";

    public MissingEncodingAlgorithmException() {
        super(MESSAGE);
    }
}
