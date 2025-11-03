package com.extole.client.rest.campaign.component.setting;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public record ComponentIdFilterCreateRequest(Set<String> componentTypes) {

    private static final String COMPONENT_TYPES = "component_types";

    @JsonCreator
    public ComponentIdFilterCreateRequest(@JsonProperty(COMPONENT_TYPES) Set<String> componentTypes) {
        this.componentTypes = ImmutableSet.copyOf(componentTypes);
    }

    @Override
    @JsonProperty(COMPONENT_TYPES)
    public Set<String> componentTypes() {
        return componentTypes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
