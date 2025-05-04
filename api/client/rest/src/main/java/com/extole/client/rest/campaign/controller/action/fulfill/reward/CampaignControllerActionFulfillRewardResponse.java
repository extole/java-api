package com.extole.client.rest.campaign.controller.action.fulfill.reward;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.FULFILL_REWARD;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionFulfillRewardResponse extends CampaignControllerActionResponse {

    private static final String JSON_REWARD_ID = "reward_id";
    private static final String JSON_MESSAGE = "message";
    private static final String JSON_SUCCESS = "success";
    private static final String JSON_EVENT_TIME = "event_time";
    private static final String JSON_PARTNER_REWARD_ID = "partner_reward_id";

    private final String rewardId;
    private final Optional<String> message;
    private final Optional<String> success;
    private final Optional<String> partnerRewardId;
    private final RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime;

    public CampaignControllerActionFulfillRewardResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_REWARD_ID) String rewardId,
        @JsonProperty(JSON_MESSAGE) Optional<String> message,
        @JsonProperty(JSON_SUCCESS) Optional<String> success,
        @JsonProperty(JSON_PARTNER_REWARD_ID) Optional<String> partnerRewardId,
        @JsonProperty(JSON_EVENT_TIME) RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime) {
        super(actionId, FULFILL_REWARD, quality, enabled, componentIds, componentReferences);
        this.rewardId = rewardId;
        this.message = message;
        this.success = success;
        this.eventTime = eventTime;
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

    @JsonProperty(JSON_EVENT_TIME)
    public RuntimeEvaluatable<RewardActionContext, Optional<Instant>> getEventTime() {
        return eventTime;
    }

}
