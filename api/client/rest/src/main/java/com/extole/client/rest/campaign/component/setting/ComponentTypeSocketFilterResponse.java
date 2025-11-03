package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentTypeSocketFilterResponse extends SocketFilterResponse {

    static final String TYPE = "COMPONENT_TYPE";

    private static final String COMPONENT_TYPE = "component_type";

    private final String componentType;

    @JsonCreator
    public ComponentTypeSocketFilterResponse(@JsonProperty(COMPONENT_TYPE) String componentType) {
        super(SocketFilterType.COMPONENT_TYPE);
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public String getComponentType() {
        return componentType;
    }

}
