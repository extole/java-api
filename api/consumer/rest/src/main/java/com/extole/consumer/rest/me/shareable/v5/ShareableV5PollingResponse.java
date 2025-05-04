package com.extole.consumer.rest.me.shareable.v5;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.common.PollingResponse;
import com.extole.consumer.rest.common.PollingStatus;

public final class ShareableV5PollingResponse implements PollingResponse {
    private static final String JSON_PROPERTY_CODE = "code";
    private static final String JSON_PROPERTY_ERROR = "error";
    private static final String JSON_PROPERTY_SHAREABLE = "shareable";

    private final String pollingId;
    private final PollingStatus status;
    private final String code;
    private final CreateMeShareableV5Error error;
    private final MeShareableV5Response meShareableResponse;

    @JsonCreator
    public ShareableV5PollingResponse(
        @JsonProperty(PollingResponse.POLLING_ID) String pollingId,
        @JsonProperty(PollingResponse.STATUS) PollingStatus status,
        @Nullable @JsonProperty(JSON_PROPERTY_CODE) String code,
        @Nullable @JsonProperty(JSON_PROPERTY_ERROR) CreateMeShareableV5Error error,
        @Nullable @JsonProperty(JSON_PROPERTY_SHAREABLE) MeShareableV5Response meShareableResponse) {
        this.pollingId = pollingId;
        this.status = status;
        this.code = code;
        this.error = error;
        this.meShareableResponse = meShareableResponse;
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

    @Nullable
    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_ERROR)
    public CreateMeShareableV5Error getError() {
        return error;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_SHAREABLE)
    public MeShareableV5Response getShareable() {
        return meShareableResponse;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
