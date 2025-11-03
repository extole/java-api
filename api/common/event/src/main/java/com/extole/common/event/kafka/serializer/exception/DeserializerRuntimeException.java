package com.extole.common.event.kafka.serializer.exception;

public class DeserializerRuntimeException extends RuntimeException {

    public DeserializerRuntimeException(String message, Throwable e) {
        super(message, e);
    }

}
