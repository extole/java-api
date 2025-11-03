package com.extole.common.event.kafka;

public class KafkaEventConsumerRuntimeException extends RuntimeException {

    public KafkaEventConsumerRuntimeException(String message) {
        super(message);
    }
}
