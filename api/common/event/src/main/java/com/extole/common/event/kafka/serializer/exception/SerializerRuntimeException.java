package com.extole.common.event.kafka.serializer.exception;

public class SerializerRuntimeException extends RuntimeException {

    public SerializerRuntimeException(String message, Throwable e) {
        super(message, e);
    }

}
