package com.extole.consumer.rest.share.custom;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.model.RestExceptionResponse;

public class CustomSharePollingResponse {
    private final String pollingId;
    private final Status status;
    private final String shareId;
    private final RestExceptionResponse error;

    public CustomSharePollingResponse(@JsonProperty("polling_id") String pollingId,
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

    @JsonProperty("share_id")
    public Optional<String> getShareId() {
        return Optional.ofNullable(shareId);
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("error")
    public Optional<RestExceptionResponse> getError() {
        return Optional.ofNullable(error);
    }

    public enum Status {
        PENDING, SUCCEEDED, FAILED
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
