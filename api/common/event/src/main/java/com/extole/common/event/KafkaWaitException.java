package com.extole.common.event;

public class KafkaWaitException extends Exception {

    private final long waitMs;

    public KafkaWaitException(String message, long waitMs) {
        super(message);
        this.waitMs = waitMs;
    }

    public long getWaitMs() {
        return waitMs;
    }
}
