package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionRevokeRewardConfiguration extends CampaignControllerActionConfiguration {

    private static final String REWARD_ID = "reward_id";
    private static final String MESSAGE = "message";

    private final String rewardId;
    private final Optional<String> message;

    public CampaignControllerActionRevokeRewardConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(MESSAGE) Optional<String> message) {
        super(actionId, CampaignControllerActionType.REVOKE_REWARD, quality, enabled, componentReferences);
        this.rewardId = rewardId;
        this.message = message;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(MESSAGE)
    public Optional<String> getMessage() {
        return message;
    }
}
