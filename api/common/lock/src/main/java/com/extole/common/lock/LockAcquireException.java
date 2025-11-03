package com.extole.common.lock;

public class LockAcquireException extends Exception {

    public LockAcquireException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockAcquireException(String message) {
        super(message);
    }
}
