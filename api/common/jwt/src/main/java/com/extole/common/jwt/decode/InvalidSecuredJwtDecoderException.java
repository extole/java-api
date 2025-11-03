package com.extole.common.jwt.decode;

import com.extole.common.jwt.JwtException;

public class InvalidSecuredJwtDecoderException extends JwtException {
    public InvalidSecuredJwtDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSecuredJwtDecoderException(String message) {
        super(message);
    }
}
