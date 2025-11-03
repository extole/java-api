package com.extole.client.rest.campaign.component.setting;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ComponentIdFilterResponse(Set<String> componentTypes) {

    private static final String COMPONENT_TYPES = "component_types";

    @JsonCreator
    public ComponentIdFilterResponse(@JsonProperty(COMPONENT_TYPES) Set<String> componentTypes) {
        this.componentTypes = componentTypes;
    }

    @Override
    @JsonProperty(COMPONENT_TYPES)
    public Set<String> componentTypes() {
        return componentTypes;
    }
}
