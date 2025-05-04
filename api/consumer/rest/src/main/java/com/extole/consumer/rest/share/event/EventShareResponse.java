package com.extole.consumer.rest.share.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove ENG-10140
public class EventShareResponse {
    private final String pollingId;
    private final String shareId;

    @JsonCreator
    public EventShareResponse(@JsonProperty("polling_id") String pollingId, @JsonProperty("share_id") String shareId) {
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

}
