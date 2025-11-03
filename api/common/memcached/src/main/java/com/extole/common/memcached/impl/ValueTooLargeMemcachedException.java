package com.extole.common.memcached.impl;

class ValueTooLargeMemcachedException extends NonTransientMemcachedClientAdapterException {

    ValueTooLargeMemcachedException(String message, Throwable cause) {
        super(message, cause);
    }

    ValueTooLargeMemcachedException(String message) {
        super(message);
    }
}
