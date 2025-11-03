package com.extole.common.jwt.encode.encrypt;

import com.extole.common.jwt.encode.JwtEncoderBuildException;

public class MissingEncryptionMethodException extends JwtEncoderBuildException {

    private static final String MESSAGE = "Cannot create encrypted jwt encoder without encryption method";

    public MissingEncryptionMethodException() {
        super(MESSAGE);
    }
}
