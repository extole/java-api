package com.extole.client.rest.campaign.built.controller.action.remove.membership;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.REMOVE_MEMBERSHIP;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;

public class BuiltCampaignControllerActionRemoveMembershipResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_AUDIENCE_ID = "audience_id";

    private final Optional<Id<?>> audienceId;

    public BuiltCampaignControllerActionRemoveMembershipResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_AUDIENCE_ID) Optional<Id<?>> audienceId,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, REMOVE_MEMBERSHIP, quality, enabled, componentIds, componentReferences);
        this.audienceId = audienceId;
    }

    @JsonProperty(JSON_AUDIENCE_ID)
    public Optional<Id<?>> getAudienceId() {
        return audienceId;
    }

}
