package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.setting.SocketFilterType;

public class ComponentTypeSocketFilterConfiguration extends SocketFilterConfiguration {

    static final String TYPE = "COMPONENT_TYPE";

    private static final String COMPONENT_TYPE = "component_type";

    private final String componentType;

    @JsonCreator
    public ComponentTypeSocketFilterConfiguration(@JsonProperty(COMPONENT_TYPE) String componentType) {
        super(SocketFilterType.COMPONENT_TYPE);
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public String getComponentType() {
        return componentType;
    }

}
