package com.extole.client.rest.campaign.built.flow.step;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltCampaignFlowStepAppResponse extends ComponentElementResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TYPE = "type";

    private final String name;
    private final Optional<String> description;
    private final BuiltCampaignFlowStepAppTypeResponse type;

    @JsonCreator
    public BuiltCampaignFlowStepAppResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_TYPE) BuiltCampaignFlowStepAppTypeResponse type,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.description = description;
        this.type = type;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_TYPE)
    public BuiltCampaignFlowStepAppTypeResponse getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
