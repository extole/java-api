package com.extole.client.rest.campaign.controller.action.step.signal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionStepSignalResponse extends CampaignControllerActionResponse {

    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_QUALITY = "quality";
    private static final String JSON_NAME = "name";

    private final String pollingId;
    private final String name;

    public CampaignControllerActionStepSignalResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_POLLING_ID) String pollingId,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.STEP_SIGNAL, quality, enabled, componentIds, componentReferences);
        this.pollingId = pollingId;
        this.name = name;
    }

    @JsonProperty(JSON_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

}
