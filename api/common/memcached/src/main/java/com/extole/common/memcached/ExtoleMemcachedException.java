package com.extole.common.memcached;

public class ExtoleMemcachedException extends Exception {

    public ExtoleMemcachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtoleMemcachedException(String message) {
        super(message);
    }
}
