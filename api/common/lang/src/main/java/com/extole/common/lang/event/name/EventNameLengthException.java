package com.extole.common.lang.event.name;

public class EventNameLengthException extends Exception {

    private final int minLength;
    private final int maxLength;

    public EventNameLengthException(String message,
        int minLength,
        int maxLength) {
        super(message);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
