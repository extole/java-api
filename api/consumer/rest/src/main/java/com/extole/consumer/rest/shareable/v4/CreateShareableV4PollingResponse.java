package com.extole.consumer.rest.shareable.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

@Deprecated // TODO remove ENG-10127
public class CreateShareableV4PollingResponse implements PollingResponse {

    private static final String SHAREABLE_ID = "shareable_id";
    private static final String ERROR = "error";
    private final String pollingId;
    private final PollingStatus status;
    private final String shareableId;
    private final CreateShareableV4Error error;

    public CreateShareableV4PollingResponse(String pollingId, PollingStatus status, String shareableId) {
        this.pollingId = pollingId;
        this.status = status;
        this.shareableId = shareableId;
        this.error = null;
    }

    public CreateShareableV4PollingResponse(String pollingId, CreateShareableV4Error error) {
        this.pollingId = pollingId;
        this.status = PollingStatus.FAILED;
        this.shareableId = null;
        this.error = error;
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

    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @JsonProperty(ERROR)
    public CreateShareableV4Error getError() {
        return error;
    }
}
