package com.extole.client.rest.campaign.configuration;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public record CampaignComponentComponentIdFilterConfiguration(Set<String> componentTypes) {

    private static final String COMPONENT_TYPES = "component_types";

    @JsonCreator
    public CampaignComponentComponentIdFilterConfiguration(@JsonProperty(COMPONENT_TYPES) Set<String> componentTypes) {
        this.componentTypes = componentTypes;
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
