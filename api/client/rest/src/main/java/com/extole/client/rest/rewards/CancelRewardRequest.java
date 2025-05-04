package com.extole.client.rest.rewards;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CancelRewardRequest {

    private static final String MESSAGE = "message";

    private final String message;

    public CancelRewardRequest(@Nullable @JsonProperty(MESSAGE) String message) {
        this.message = message;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
