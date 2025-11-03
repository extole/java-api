package com.extole.common.jwt;

public class JwtRuntimeException extends RuntimeException {

    public JwtRuntimeException(String message) {
        super(message);
    }

    public JwtRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
