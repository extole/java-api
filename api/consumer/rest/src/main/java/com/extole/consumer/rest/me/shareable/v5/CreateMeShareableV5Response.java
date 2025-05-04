package com.extole.consumer.rest.me.shareable.v5;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CreateMeShareableV5Response {
    private static final String JSON_PROPERTY_POLLING_ID = "polling_id";

    private final String pollingId;

    public CreateMeShareableV5Response(@JsonProperty(JSON_PROPERTY_POLLING_ID) String pollingId) {
        this.pollingId = pollingId;
    }

    @JsonProperty(JSON_PROPERTY_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
