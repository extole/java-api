package com.extole.common.event.kafka.producer;

public class KafkaEventProducerRuntimeException extends RuntimeException {

    public KafkaEventProducerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaEventProducerRuntimeException(String message) {
        super(message);
    }
}
