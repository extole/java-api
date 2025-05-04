package com.extole.consumer.rest.me;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

public class VerificationEmailResponse implements PollingResponse {

    private final String pollingId;
    private final PollingStatus status;

    public VerificationEmailResponse(@JsonProperty(PollingResponse.POLLING_ID) String pollingId,
        @Nullable @JsonProperty(PollingResponse.STATUS) PollingStatus status) {
        this.pollingId = pollingId;
        this.status = status;
    }

    @JsonProperty(POLLING_ID)
    @Override
    public String getPollingId() {
        return pollingId;
    }

    @Override
    @Nullable
    @JsonProperty(STATUS)
    public PollingStatus getStatus() {
        return status;
    }
}
