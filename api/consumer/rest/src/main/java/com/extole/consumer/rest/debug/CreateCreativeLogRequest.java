package com.extole.consumer.rest.debug;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CreateCreativeLogRequest {
    private static final String MESSAGE = "message";
    private static final String LEVEL = "level";
    private final String message;
    private final CreativeLogLevel level;

    @JsonCreator
    public CreateCreativeLogRequest(@JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(LEVEL) CreativeLogLevel level) {
        this.message = message;
        this.level = level;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Nullable
    @JsonProperty(LEVEL)
    public CreativeLogLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
