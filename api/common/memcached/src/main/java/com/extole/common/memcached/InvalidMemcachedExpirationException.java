package com.extole.common.memcached;

public class InvalidMemcachedExpirationException extends Exception {

    public InvalidMemcachedExpirationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMemcachedExpirationException(String message) {
        super(message);
    }

}
