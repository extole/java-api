package com.extole.consumer.rest.shareable.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

@Deprecated // TODO remove ENG-10127
public class ShareableCreateV4PollingResponse implements PollingResponse {

    private static final String SHAREABLE_ID = "shareable_id";
    private static final String ERROR = "error";
    private final String pollingId;
    private final PollingStatus status;
    private final String shareableId;
    private final ShareableCreateV4Error error;

    public ShareableCreateV4PollingResponse(
        @JsonProperty(PollingResponse.POLLING_ID) String pollingId,
        @Nullable @JsonProperty(SHAREABLE_ID) String shareableId,
        @JsonProperty(PollingResponse.STATUS) PollingStatus status,
        @Nullable @JsonProperty(ERROR) ShareableCreateV4Error error) {
        this.pollingId = pollingId;
        this.status = status;
        this.shareableId = shareableId;
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

    @Nullable
    @JsonProperty(SHAREABLE_ID)
    public String getShareableId() {
        return shareableId;
    }

    @Nullable
    @JsonProperty(ERROR)
    public ShareableCreateV4Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
