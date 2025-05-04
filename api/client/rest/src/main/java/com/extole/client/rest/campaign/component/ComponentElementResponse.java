package com.extole.client.rest.campaign.component;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.id.Id;

public abstract class ComponentElementResponse {

    protected static final String JSON_COMPONENT_REFERENCES = "component_references";
    protected static final String JSON_COMPONENT_IDS = "component_ids";

    private final List<ComponentReferenceResponse> componentReferences;
    private final List<Id<ComponentResponse>> componentIds;

    public ComponentElementResponse(
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds) {
        this.componentReferences = componentReferences;
        this.componentIds = componentIds;
    }

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public List<ComponentReferenceResponse> getComponentReferences() {
        return componentReferences;
    }

    @Deprecated // TODO remove after UI changes ENG-23427
    @JsonProperty(JSON_COMPONENT_IDS)
    public List<Id<ComponentResponse>> getComponentIds() {
        return componentIds;
    }

}
