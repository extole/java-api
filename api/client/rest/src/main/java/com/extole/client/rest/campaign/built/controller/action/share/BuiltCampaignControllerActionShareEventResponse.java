package com.extole.client.rest.campaign.built.controller.action.share;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.id.Id;

public class BuiltCampaignControllerActionShareEventResponse extends BuiltCampaignControllerActionResponse {

    public BuiltCampaignControllerActionShareEventResponse(@JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.SHARE_EVENT, quality, enabled, componentIds, componentReferences);
    }

}
