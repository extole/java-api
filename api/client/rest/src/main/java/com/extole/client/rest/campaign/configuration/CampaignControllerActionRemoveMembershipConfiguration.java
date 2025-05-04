package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionRemoveMembershipConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_AUDIENCE_ID = "audience_id";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> audienceId;

    public CampaignControllerActionRemoveMembershipConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_AUDIENCE_ID) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> audienceId,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.REMOVE_MEMBERSHIP, quality, enabled, componentReferences);
        this.audienceId = audienceId;
    }

    @JsonProperty(JSON_AUDIENCE_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> getAudienceId() {
        return audienceId;
    }

}
