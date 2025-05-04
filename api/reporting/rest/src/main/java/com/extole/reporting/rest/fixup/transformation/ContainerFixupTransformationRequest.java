package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerFixupTransformationRequest {
    private static final String JSON_CONTAINER = "container";

    private final String container;

    @JsonCreator
    public ContainerFixupTransformationRequest(@JsonProperty(JSON_CONTAINER) String container) {
        this.container = container;
    }

    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return container;
    }
}
