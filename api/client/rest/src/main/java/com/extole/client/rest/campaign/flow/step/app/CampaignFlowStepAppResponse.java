package com.extole.client.rest.campaign.flow.step.app;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepAppResponse extends ComponentElementResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TYPE = "type";

    private final String id;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description;
    private final CampaignFlowStepAppTypeResponse type;

    @JsonCreator
    public CampaignFlowStepAppResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_TYPE) CampaignFlowStepAppTypeResponse type,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_TYPE)
    public CampaignFlowStepAppTypeResponse getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
