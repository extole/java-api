package com.extole.common.jwt.encode;

public class MissingEncodingKeyException extends JwtEncoderBuildException {

    private static final String MESSAGE = "Cannot create encoder without key";

    public MissingEncodingKeyException() {
        super(MESSAGE);
    }
}
