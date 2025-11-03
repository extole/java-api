package com.extole.common.memcached.impl;

abstract class MemcachedClientAdapterException extends Exception {

    MemcachedClientAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    MemcachedClientAdapterException(String message) {
        super(message);
    }

    MemcachedClientAdapterException(Throwable cause) {
        super(cause);
    }
}
