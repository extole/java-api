package com.extole.consumer.rest.me;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

public class UpdateProfileResponse implements PollingResponse {

    private final String pollingId;
    private final PollingStatus status;

    @JsonCreator
    public UpdateProfileResponse(
        @JsonProperty(POLLING_ID) String pollingId,
        @JsonProperty(STATUS) PollingStatus status) {
        this.pollingId = pollingId;
        this.status = status;
    }

    @Override
    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @Override
    @JsonProperty(STATUS)
    public PollingStatus getStatus() {
        return status;
    }

}
