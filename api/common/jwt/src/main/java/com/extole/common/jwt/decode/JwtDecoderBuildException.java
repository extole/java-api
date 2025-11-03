package com.extole.common.jwt.decode;

import com.extole.common.jwt.JwtException;

public abstract class JwtDecoderBuildException extends JwtException {

    public JwtDecoderBuildException(String message) {
        super(message);
    }
}
