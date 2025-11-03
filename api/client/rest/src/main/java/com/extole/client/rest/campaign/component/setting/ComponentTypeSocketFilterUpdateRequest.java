package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ComponentTypeSocketFilterUpdateRequest extends SocketFilterUpdateRequest {

    static final String TYPE = "COMPONENT_TYPE";

    private static final String COMPONENT_TYPE = "component_type";

    private final Omissible<String> componentType;

    @JsonCreator
    public ComponentTypeSocketFilterUpdateRequest(@JsonProperty(COMPONENT_TYPE) Omissible<String> componentType) {
        super(SocketFilterType.COMPONENT_TYPE);
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public Omissible<String> getComponentType() {
        return componentType;
    }

}
