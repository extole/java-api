package com.extole.common.jwt.encode;

import com.extole.common.jwt.JwtException;

public abstract class JwtEncoderBuildException extends JwtException {

    public JwtEncoderBuildException(String message) {
        super(message);
    }

    public JwtEncoderBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
