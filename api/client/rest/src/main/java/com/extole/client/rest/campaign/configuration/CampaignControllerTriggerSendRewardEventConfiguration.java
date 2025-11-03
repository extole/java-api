package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerSendRewardEventConfiguration extends CampaignControllerTriggerConfiguration {

    @Schema
    public enum RewardState {
        FULFILLED,
        SENT
    }

    private static final String REWARD_STATES = "reward_states";
    private static final String REWARD_NAMES = "reward_names";
    private static final String TAGS = "tags";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> rewardNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags;

    public CampaignControllerTriggerSendRewardEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(REWARD_STATES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates,
        @JsonProperty(REWARD_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> rewardNames,
        @JsonProperty(TAGS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.SEND_REWARD_EVENT,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.rewardStates = rewardStates;
        this.rewardNames = rewardNames;
        this.tags = tags;
    }

    @JsonProperty(REWARD_STATES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(REWARD_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getRewardNames() {
        return rewardNames;
    }

    @JsonProperty(TAGS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getTags() {
        return tags;
    }

}
