package com.extole.common.memcached.impl;

class TransientMemcachedClientAdapterException extends MemcachedClientAdapterException {

    TransientMemcachedClientAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    TransientMemcachedClientAdapterException(String message) {
        super(message);
    }
}
