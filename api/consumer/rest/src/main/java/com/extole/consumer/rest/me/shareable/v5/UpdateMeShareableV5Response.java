package com.extole.consumer.rest.me.shareable.v5;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateMeShareableV5Response {
    private static final String JSON_PROPERTY_POLLING_ID = "polling_id";

    private final String pollingId;

    public UpdateMeShareableV5Response(@JsonProperty(JSON_PROPERTY_POLLING_ID) String pollingId) {
        this.pollingId = pollingId;
    }

    @JsonProperty(JSON_PROPERTY_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

}
