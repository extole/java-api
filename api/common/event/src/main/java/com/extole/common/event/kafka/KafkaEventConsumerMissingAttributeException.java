package com.extole.common.event.kafka;

public class KafkaEventConsumerMissingAttributeException extends IllegalArgumentException {
    public KafkaEventConsumerMissingAttributeException(String message) {
        super(message);
    }
}
