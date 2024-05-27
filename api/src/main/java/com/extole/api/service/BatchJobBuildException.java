package com.extole.api.service;

public class BatchJobBuildException extends Exception {
    public BatchJobBuildException(String message) {
        super(message);
    }

    public BatchJobBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchJobBuildException(Throwable cause) {
        super(cause);
    }
}
