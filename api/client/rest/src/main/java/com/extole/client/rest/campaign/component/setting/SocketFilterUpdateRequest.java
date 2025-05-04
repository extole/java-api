package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class SocketFilterUpdateRequest {

    private static final String COMPONENT_TYPE = "component_type";

    private final Omissible<String> componentType;

    @JsonCreator
    public SocketFilterUpdateRequest(@JsonProperty(COMPONENT_TYPE) Omissible<String> componentType) {
        this.componentType = componentType;
    }

    @JsonProperty(COMPONENT_TYPE)
    public Omissible<String> getComponentType() {
        return componentType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
