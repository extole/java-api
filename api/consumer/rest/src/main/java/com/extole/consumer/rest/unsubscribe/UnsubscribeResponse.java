package com.extole.consumer.rest.unsubscribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class UnsubscribeResponse {
    private static final String UNSUBSCRIBED = "unsubscribed";

    private final Boolean unsubscribed;

    @JsonCreator
    public UnsubscribeResponse(@JsonProperty(UNSUBSCRIBED) Boolean unsubscribed) {
        this.unsubscribed = unsubscribed;
    }

    @JsonProperty(UNSUBSCRIBED)
    public Boolean getUnsubscribed() {
        return unsubscribed;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
