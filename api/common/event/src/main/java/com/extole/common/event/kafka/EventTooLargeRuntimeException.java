package com.extole.common.event.kafka;

public class EventTooLargeRuntimeException extends RuntimeException {

    public EventTooLargeRuntimeException(String message, EventTooLargeException e) {
        super(message, e);
    }

    public EventTooLargeRuntimeException(EventTooLargeException e) {
        super(e);
    }
}
