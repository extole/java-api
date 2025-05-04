package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PiiObfuscateFixupTransformationResponse extends FixupTransformationResponse {

    private static final String JSON_REQUEST_ID = "request_id";

    private final String requestId;

    @JsonCreator
    public PiiObfuscateFixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type,
        @JsonProperty(JSON_REQUEST_ID) String requestId) {
        super(id, type);
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

}
