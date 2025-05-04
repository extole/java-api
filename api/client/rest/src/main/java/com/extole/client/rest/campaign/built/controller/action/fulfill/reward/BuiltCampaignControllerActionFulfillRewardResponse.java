package com.extole.client.rest.campaign.built.controller.action.fulfill.reward;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.FULFILL_REWARD;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;

public class BuiltCampaignControllerActionFulfillRewardResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_REWARD_ID = "reward_id";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_PARTNER_REWARD_ID = "partner_reward_id";

    private final String rewardId;
    private final Optional<String> message;
    private final Optional<String> success;
    private final Optional<String> partnerRewardId;

    public BuiltCampaignControllerActionFulfillRewardResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_REWARD_ID) String rewardId,
        @JsonProperty(JSON_MESSAGE) Optional<String> message,
        @JsonProperty(JSON_SUCCESS) Optional<String> success,
        @JsonProperty(JSON_PARTNER_REWARD_ID) Optional<String> partnerRewardId) {
        super(actionId, FULFILL_REWARD, quality, enabled, componentIds, componentReferences);
        this.rewardId = rewardId;
        this.message = message;
        this.success = success;
        this.partnerRewardId = partnerRewardId;
    }

    @JsonProperty(JSON_REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(JSON_MESSAGE)
    public Optional<String> getMessage() {
        return message;
    }

    @JsonProperty(JSON_SUCCESS)
    public Optional<String> getSuccess() {
        return success;
    }

    @JsonProperty(JSON_PARTNER_REWARD_ID)
    public Optional<String> getPartnerRewardId() {
        return partnerRewardId;
    }

}
