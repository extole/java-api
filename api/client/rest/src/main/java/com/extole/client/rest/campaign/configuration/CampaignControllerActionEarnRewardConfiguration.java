package com.extole.client.rest.campaign.configuration;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.reward.RewardActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerActionEarnRewardConfiguration extends CampaignControllerActionConfiguration {

    private static final String REWARD_NAME = "reward_name";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String EVENT_TIME = "event_time";
    private static final String VALUE_OF_EVENT_BEING_REWARDED = "value_of_event_being_rewarded";
    private static final String REWARD_ACTION_ID = "reward_action_id";
    private static final String EXTRA_DATA = "extra_data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> rewardName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> rewardSupplierId;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Set<String>>> tags;
    private final Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
        BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Optional<Object>>> valueOfEventBeingRewarded;
    private final RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime;
    private final RuntimeEvaluatable<RewardActionContext, Id<?>> rewardActionId;
    private final BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Map<String, Optional<Object>>>> extraData;

    public CampaignControllerActionEarnRewardConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(REWARD_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> rewardName,
        @JsonProperty(REWARD_SUPPLIER_ID) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<Id<?>>> rewardSupplierId,
        @JsonProperty(TAGS) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Set<String>>> tags,
        @JsonProperty(DATA) Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> data,
        @JsonProperty(VALUE_OF_EVENT_BEING_REWARDED) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Optional<Object>>> valueOfEventBeingRewarded,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(EVENT_TIME) RuntimeEvaluatable<RewardActionContext, Optional<Instant>> eventTime,
        @JsonProperty(REWARD_ACTION_ID) RuntimeEvaluatable<RewardActionContext, Id<?>> rewardActionId,
        @JsonProperty(EXTRA_DATA) BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<RewardActionContext, Map<String, Optional<Object>>>> extraData) {
        super(actionId, CampaignControllerActionType.EARN_REWARD, quality, enabled, componentReferences);
        this.rewardName = rewardName;
        this.rewardSupplierId = rewardSupplierId;
        this.tags = tags;
        Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<RewardActionContext, Optional<Object>>>> calculatedData =
                    data != null ? new HashMap<>(data) : new HashMap<>();
        if (!calculatedData.containsKey(Provided.of("earned_event_value"))) {
            if (valueOfEventBeingRewarded != null) {
                calculatedData.put(Provided.of("earned_event_value"), valueOfEventBeingRewarded);
            }
        }
        this.data = Collections.unmodifiableMap(calculatedData);
        this.valueOfEventBeingRewarded = valueOfEventBeingRewarded;
        this.eventTime = eventTime;
        this.rewardActionId = rewardActionId;
        this.extraData = extraData;
    }

    @JsonProperty(REWARD_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getRewardName() {
        return rewardName;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<Id<?>>> getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(TAGS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<RewardActionContext, Set<String>>>
        getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
        BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<RewardActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(VALUE_OF_EVENT_BEING_REWARDED)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<RewardActionContext, Optional<Object>>>
        getValueOfEventBeingRewarded() {
        return valueOfEventBeingRewarded;
    }

    @JsonProperty(EVENT_TIME)
    public RuntimeEvaluatable<RewardActionContext, Optional<Instant>> getEventTime() {
        return eventTime;
    }

    @JsonProperty(REWARD_ACTION_ID)
    public RuntimeEvaluatable<RewardActionContext, Id<?>> getRewardActionId() {
        return rewardActionId;
    }

    @JsonProperty(EXTRA_DATA)
    public BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<RewardActionContext, Map<String, Optional<Object>>>> getExtraData() {
        return extraData;
    }

}
