package com.extole.common.memcached;

import java.io.UnsupportedEncodingException;

public class MalformedMemcachedKeyException extends ExtoleMemcachedException {

    public MalformedMemcachedKeyException(String message) {
        super(message);
    }

    public MalformedMemcachedKeyException(String message, UnsupportedEncodingException e) {
        super(message, e);
    }

}
