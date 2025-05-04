package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerFixupTransformationResponse extends FixupTransformationResponse {

    private static final String JSON_CONTAINER = "container";

    private final String container;

    @JsonCreator
    public ContainerFixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type,
        @JsonProperty(JSON_CONTAINER) String container) {
        super(id, type);
        this.container = container;
    }

    public String getContainer() {
        return container;
    }
}
