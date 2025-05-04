package com.extole.consumer.rest.me.email;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SendEmailResponse {
    private static final String POLLING_ID = "polling_id";

    private final String pollingId;

    public SendEmailResponse(@JsonProperty(POLLING_ID) String pollingId) {
        this.pollingId = pollingId;
    }

    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
