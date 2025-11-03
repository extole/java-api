package com.extole.common.memcached;

public class OutdatedCasVersionException extends Exception {

    public OutdatedCasVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutdatedCasVersionException(String message) {
        super(message);
    }
}
