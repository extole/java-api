package com.extole.consumer.rest.me.reward;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClaimRewardResponse {
    private static final String POLLING_ID = "polling_id";

    private final String pollingId;

    public ClaimRewardResponse(@JsonProperty(POLLING_ID) String pollingId) {
        this.pollingId = pollingId;
    }

    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

}
