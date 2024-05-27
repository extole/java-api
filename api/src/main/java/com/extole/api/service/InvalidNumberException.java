package com.extole.api.service;

public class InvalidNumberException extends Exception {
    public InvalidNumberException(String message) {
        super(message);
    }

    public InvalidNumberException(String message, Throwable cause) {
        super(message, cause);
    }

}
