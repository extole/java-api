package com.extole.common.lock;

public class MalformedKeyException extends Exception {

    public MalformedKeyException(String message, Exception e) {
        super(message, e);
    }

    public MalformedKeyException(String message) {
        super(message);
    }
}
