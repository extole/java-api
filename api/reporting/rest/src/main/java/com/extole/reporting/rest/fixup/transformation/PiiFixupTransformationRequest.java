package com.extole.reporting.rest.fixup.transformation;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PiiFixupTransformationRequest {
    private static final String JSON_REQUEST_ID = "request_id";

    private final String requestId;

    @JsonCreator
    public PiiFixupTransformationRequest(@Nullable @JsonProperty(JSON_REQUEST_ID) String requestId) {
        this.requestId = requestId;
    }

    @Nullable
    @JsonProperty(JSON_REQUEST_ID)
    public String getRequestId() {
        return requestId;
    }
}
