package com.extole.client.rest.campaign.controller.action.create.membership;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.CREATE_MEMBERSHIP;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionCreateMembershipResponse extends CampaignControllerActionResponse {

    private static final String JSON_AUDIENCE_ID = "audience_id";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> audienceId;

    public CampaignControllerActionCreateMembershipResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_AUDIENCE_ID) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> audienceId,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CREATE_MEMBERSHIP, quality, enabled, componentIds, componentReferences);
        this.audienceId = audienceId;
    }

    @JsonProperty(JSON_AUDIENCE_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> getAudienceId() {
        return audienceId;
    }

}
