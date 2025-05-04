package com.extole.consumer.rest.share.event;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.model.RestExceptionResponse;

@Deprecated // TODO remove ENG-10140
public class EventSharePollingResponse {
    private final String pollingId;
    private final Status status;
    private final String shareId;
    private final RestExceptionResponse error;

    public EventSharePollingResponse(@JsonProperty("polling_id") String pollingId,
        @Nullable @JsonProperty("share_id") String shareId,
        @JsonProperty("status") Status status,
        @Nullable @JsonProperty("error") RestExceptionResponse error) {
        this.pollingId = pollingId;
        this.shareId = shareId;
        this.status = status;
        this.error = error;
    }

    @JsonProperty("polling_id")
    public String getPollingId() {
        return pollingId;
    }

    @Nullable
    @JsonProperty("share_id")
    public String getShareId() {
        return shareId;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @Nullable
    @JsonProperty("error")
    public RestExceptionResponse getError() {
        return error;
    }

    public enum Status {
        PENDING, SUCCEEDED, FAILED
    }
}
