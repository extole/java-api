package com.extole.common.event.kafka.producer;

public class KafkaSendEventFailureException extends Exception {

    public KafkaSendEventFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaSendEventFailureException(String message) {
        super(message);
    }
}
