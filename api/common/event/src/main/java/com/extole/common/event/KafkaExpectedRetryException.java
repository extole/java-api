package com.extole.common.event;

public class KafkaExpectedRetryException extends KafkaRetryException {
    public KafkaExpectedRetryException(String message) {
        super(message);
    }
}
