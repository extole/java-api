package com.extole.common.jwt.decode;

public class MissingDecodingKeyException extends JwtDecoderBuildException {

    private static final String MESSAGE = "Cannot create secured JWT decoder without key";

    public MissingDecodingKeyException() {
        super(MESSAGE);
    }
}
