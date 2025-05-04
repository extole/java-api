package com.extole.client.rest.campaign.built.controller.action.earn.reward;

import static com.extole.client.rest.campaign.controller.action.CampaignControllerActionType.EARN_REWARD;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionEarnRewardResponse extends BuiltCampaignControllerActionResponse {

    private static final String REWARD_NAME = "reward_name";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String VALUE_OF_EVENT_BEING_REWARDED = "value_of_event_being_rewarded";
    private static final String REWARD_ACTION_ID = "reward_action_id";

    private final String rewardName;
    private final Optional<String> rewardSupplierId;
    private final RuntimeEvaluatable<RewardActionContext, Set<String>> tags;
    private final Map<String, RuntimeEvaluatable<RewardActionContext, Optional<Object>>> data;
    private final RuntimeEvaluatable<RewardActionContext, Optional<Object>> valueOfEventBeingRewarded;
    private final RuntimeEvaluatable<RewardActionContext, Id<?>> rewardActionId;

    public BuiltCampaignControllerActionEarnRewardResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(REWARD_NAME) String rewardName,
        @JsonProperty(REWARD_SUPPLIER_ID) Optional<String> rewardSupplierId,
        @JsonProperty(TAGS) RuntimeEvaluatable<RewardActionContext, Set<String>> tags,
        @JsonProperty(DATA) Map<String, RuntimeEvaluatable<RewardActionContext, Optional<Object>>> data,
        @JsonProperty(VALUE_OF_EVENT_BEING_REWARDED) RuntimeEvaluatable<RewardActionContext,
            Optional<Object>> valueOfEventBeingRewarded,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(REWARD_ACTION_ID) RuntimeEvaluatable<RewardActionContext, Id<?>> rewardActionId) {
        super(actionId, EARN_REWARD, quality, enabled, componentIds, componentReferences);
        this.rewardName = rewardName;
        this.rewardSupplierId = rewardSupplierId;
        this.tags = tags;
        this.data = data;
        this.valueOfEventBeingRewarded = valueOfEventBeingRewarded;
        this.rewardActionId = rewardActionId;
    }

    @JsonProperty(REWARD_NAME)
    public String getRewardName() {
        return rewardName;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public Optional<String> getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(SLOTS)
    public RuntimeEvaluatable<RewardActionContext, Set<String>> getSlots() {
        return tags;
    }

    @JsonProperty(TAGS)
    public RuntimeEvaluatable<RewardActionContext, Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<String, RuntimeEvaluatable<RewardActionContext, Optional<Object>>> getData() {
        return data;
    }

    @JsonProperty(VALUE_OF_EVENT_BEING_REWARDED)
    public RuntimeEvaluatable<RewardActionContext, Optional<Object>> getValueOfEventBeingRewarded() {
        return valueOfEventBeingRewarded;
    }

    @JsonProperty(REWARD_ACTION_ID)
    public RuntimeEvaluatable<RewardActionContext, Id<?>> getRewardActionId() {
        return rewardActionId;
    }

}
