package com.extole.client.rest.campaign.component.setting;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public record ComponentIdFilterUpdateRequest(Omissible<Set<String>> componentTypes) {

    private static final String COMPONENT_TYPES = "component_types";

    @JsonCreator
    public ComponentIdFilterUpdateRequest(@JsonProperty(COMPONENT_TYPES) Omissible<Set<String>> componentTypes) {
        this.componentTypes = componentTypes;
    }

    @Override
    @JsonProperty(COMPONENT_TYPES)
    public Omissible<Set<String>> componentTypes() {
        return componentTypes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
