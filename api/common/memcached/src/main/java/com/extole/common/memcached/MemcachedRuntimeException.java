package com.extole.common.memcached;

public class MemcachedRuntimeException extends RuntimeException {

    public MemcachedRuntimeException(Throwable cause) {
        super(cause);
    }

    public MemcachedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
