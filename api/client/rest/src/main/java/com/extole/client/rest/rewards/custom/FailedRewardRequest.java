package com.extole.client.rest.rewards.custom;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class FailedRewardRequest {

    private static final String MESSAGE = "message";

    private final String message;

    public FailedRewardRequest(@Nullable @JsonProperty(MESSAGE) String message) {
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
