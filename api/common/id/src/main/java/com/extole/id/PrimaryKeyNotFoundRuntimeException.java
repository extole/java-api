package com.extole.id;

public class PrimaryKeyNotFoundRuntimeException extends RuntimeException {

    public PrimaryKeyNotFoundRuntimeException() {
        super();
    }

    public PrimaryKeyNotFoundRuntimeException(String message) {
        super(message);
    }

    public PrimaryKeyNotFoundRuntimeException(Throwable cause) {
        super(cause);
    }

    public PrimaryKeyNotFoundRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
