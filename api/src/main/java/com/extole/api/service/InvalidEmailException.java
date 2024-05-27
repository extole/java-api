package com.extole.api.service;

public class InvalidEmailException extends Exception {

    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEmailException(String message) {
        super(message);
    }
}
