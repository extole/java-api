package com.extole.common.jwt.decode;

import com.extole.common.jwt.JwtException;

public class JwtParseException extends JwtException {

    public JwtParseException(String message) {
        super(message);
    }

    public JwtParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
