package com.extole.client.rest.campaign.built.controller.action.step.signal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.id.Id;

public class BuiltCampaignControllerActionStepSignalResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_NAME = "name";

    private final String pollingId;
    private final String name;

    public BuiltCampaignControllerActionStepSignalResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_POLLING_ID) String pollingId,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
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
