package com.extole.consumer.rest.share.email;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.model.RestExceptionResponse;

public class EmailSharePollingResponse {
    private final String pollingId;
    private final String shareId;
    private final Status status;
    private final RestExceptionResponse error;

    public EmailSharePollingResponse(
        @JsonProperty("polling_id") String pollingId,
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
