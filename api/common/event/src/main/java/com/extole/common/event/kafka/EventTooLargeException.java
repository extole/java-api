package com.extole.common.event.kafka;

public class EventTooLargeException extends Exception {
    private final int eventSize;
    private final int maxRequestSizeBytes;

    public EventTooLargeException(String message, int eventSize, int maxRequestSizeBytes) {
        super(message);
        this.eventSize = eventSize;
        this.maxRequestSizeBytes = maxRequestSizeBytes;
    }

    public int getEventSize() {
        return eventSize;
    }

    public int getMaxRequestSizeBytes() {
        return maxRequestSizeBytes;
    }
}
