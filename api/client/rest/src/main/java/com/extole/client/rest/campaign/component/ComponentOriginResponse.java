package com.extole.client.rest.campaign.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentOriginResponse {

    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_COMPONENT_ID = "component_id";
    private static final String JSON_COMPONENT_VERSION = "component_version";

    private final String clientId;
    private final String componentId;
    private final Integer componentVersion;

    @JsonCreator
    public ComponentOriginResponse(
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_COMPONENT_ID) String componentId,
        @JsonProperty(JSON_COMPONENT_VERSION) Integer componentVersion) {
        this.clientId = clientId;
        this.componentId = componentId;
        this.componentVersion = componentVersion;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_COMPONENT_ID)
    public String getComponentId() {
        return componentId;
    }

    @JsonProperty(JSON_COMPONENT_VERSION)
    public Integer getComponentVersion() {
        return componentVersion;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
