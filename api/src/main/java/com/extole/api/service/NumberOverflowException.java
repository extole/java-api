package com.extole.api.service;

public class NumberOverflowException extends Exception {
    public NumberOverflowException(String message) {
        super(message);
    }

    public NumberOverflowException(String message, Throwable cause) {
        super(message, cause);
    }

}
