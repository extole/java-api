package com.extole.client.rest.campaign.component;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public class ComponentReferenceResponse {

    private static final String COMPONENT_ID = "component_id";
    private static final String SOCKET_NAMES = "socket_names";

    private final Id<ComponentResponse> componentId;
    private final List<String> socketNames;

    public ComponentReferenceResponse(@JsonProperty(COMPONENT_ID) Id<ComponentResponse> componentId,
        @JsonProperty(SOCKET_NAMES) List<String> socketNames) {
        this.componentId = componentId;
        this.socketNames = socketNames;
    }

    @JsonProperty(COMPONENT_ID)
    public Id<ComponentResponse> getComponentId() {
        return componentId;
    }

    @JsonProperty(SOCKET_NAMES)
    public List<String> getSocketNames() {
        return socketNames;
    }

}
