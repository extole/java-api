package com.extole.client.rest.campaign.built.controller.action.cancel.reward;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.CANCEL_REWARD;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;

public class BuiltCampaignControllerActionCancelRewardResponse extends BuiltCampaignControllerActionResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String MESSAGE = "message";

    private final String rewardId;
    private final Optional<String> message;

    public BuiltCampaignControllerActionCancelRewardResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(MESSAGE) Optional<String> message) {
        super(actionId, CANCEL_REWARD, quality, enabled, componentIds, componentReferences);
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
