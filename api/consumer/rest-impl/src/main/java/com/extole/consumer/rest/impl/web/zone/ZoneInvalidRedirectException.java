package com.extole.consumer.rest.impl.web.zone;

public class ZoneInvalidRedirectException extends Exception {
    public ZoneInvalidRedirectException(String message) {
        super(message);
    }

    public ZoneInvalidRedirectException(String message, Throwable cause) {
        super(message, cause);
    }
}
