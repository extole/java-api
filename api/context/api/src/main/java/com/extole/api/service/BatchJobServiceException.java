package com.extole.api.service;

public class BatchJobServiceException extends Exception {
    public BatchJobServiceException(String message) {
        super(message);
    }

    public BatchJobServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchJobServiceException(Throwable cause) {
        super(cause);
    }
}
