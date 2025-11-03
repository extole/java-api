package com.extole.common.lock;

public class LockServiceException extends Exception {

    public LockServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockServiceException(String message) {
        super(message);
    }
}
