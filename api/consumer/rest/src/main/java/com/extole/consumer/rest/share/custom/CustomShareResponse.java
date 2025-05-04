package com.extole.consumer.rest.share.custom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CustomShareResponse {
    private final String pollingId;
    private final String shareId;

    @JsonCreator
    public CustomShareResponse(@JsonProperty("polling_id") String pollingId, @JsonProperty("share_id") String shareId) {
        this.pollingId = pollingId;
        this.shareId = shareId;
    }

    @JsonProperty("polling_id")
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty("share_id")
    public String getShareId() {
        return shareId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
