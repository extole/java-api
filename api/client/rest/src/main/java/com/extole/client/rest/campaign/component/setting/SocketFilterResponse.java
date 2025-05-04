package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SocketFilterResponse {

    private static final String COMPONENT_TYPE = "component_type";

    private final String componentType;

    @JsonCreator
    public SocketFilterResponse(@JsonProperty(COMPONENT_TYPE) String componentType) {
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public String getComponentType() {
        return componentType;
    }

}
