package com.extole.client.rest.impl.me;

public class ZendeskSingleSignOnException extends Exception {
    public ZendeskSingleSignOnException(String message) {
        super(message);
    }

    public ZendeskSingleSignOnException(String message, Throwable e) {
        super(message, e);
    }
}
