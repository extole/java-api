package com.extole.consumer.rest.me.shareable.v4;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove ENG-10127
public class CreateMeShareableV4Response {

    private static final String JSON_PROPERTY_POLLING_ID = "polling_id";
    private final String pollingId;

    public CreateMeShareableV4Response(@JsonProperty(JSON_PROPERTY_POLLING_ID) String pollingId) {
        this.pollingId = pollingId;
    }

    @JsonProperty(JSON_PROPERTY_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

}
