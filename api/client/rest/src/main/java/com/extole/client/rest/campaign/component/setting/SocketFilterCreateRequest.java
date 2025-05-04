package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SocketFilterCreateRequest {

    private static final String COMPONENT_TYPE = "component_type";

    private final String componentType;

    @JsonCreator
    public SocketFilterCreateRequest(@JsonProperty(COMPONENT_TYPE) String componentType) {
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public String getComponentType() {
        return componentType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
