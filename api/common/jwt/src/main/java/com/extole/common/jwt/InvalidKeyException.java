package com.extole.common.jwt;

public abstract class InvalidKeyException extends JwtException {

    public InvalidKeyException(String message) {
        super(message);
    }

    public InvalidKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
