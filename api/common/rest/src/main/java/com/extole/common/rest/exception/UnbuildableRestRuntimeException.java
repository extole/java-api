package com.extole.common.rest.exception;

public class UnbuildableRestRuntimeException extends RuntimeException {

    public UnbuildableRestRuntimeException(String message) {
        super(message);
    }

    public UnbuildableRestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
