package com.extole.common.memcached.impl;

class NonTransientMemcachedClientAdapterException extends MemcachedClientAdapterException {

    NonTransientMemcachedClientAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    NonTransientMemcachedClientAdapterException(String message) {
        super(message);
    }
}
