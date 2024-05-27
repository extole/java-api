package com.extole.api.service;

public class JwtBuilderException extends Exception {

    public JwtBuilderException() {
        super();
    }

    public JwtBuilderException(String message) {
        super(message);
    }

    public JwtBuilderException(Throwable cause) {
        super(cause);
    }

    public JwtBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

}
