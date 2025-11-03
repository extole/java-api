package com.extole.common.event;

public class KafkaRetryException extends Exception {

    public KafkaRetryException(String message, Throwable e) {
        super(message, e);
    }

    public KafkaRetryException(String message) {
        super(message);
    }
}
