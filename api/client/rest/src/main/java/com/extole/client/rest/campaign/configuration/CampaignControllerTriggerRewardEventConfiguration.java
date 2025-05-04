package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerRewardEventConfiguration extends CampaignControllerTriggerConfiguration {

    @Schema
    public enum RewardState {
        EARNED,
        FULFILLED,
        CANCELED,
        REVOKED,
        REDEEMED,
        FAILED
    }

    private static final String REWARD_STATES = "reward_states";
    private static final String EVENT_NAMES = "event_names";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags;

    public CampaignControllerTriggerRewardEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(REWARD_STATES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates,
        @JsonProperty(EVENT_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames,
        @JsonProperty(SLOTS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> slots,
        @JsonProperty(TAGS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.REWARD_EVENT, triggerPhase, name, description, enabled,
            negated, componentReferences);
        this.rewardStates = rewardStates;
        this.eventNames = eventNames;
        this.tags = Evaluatable.defaultIfUndefined(tags, slots);
    }

    @JsonProperty(REWARD_STATES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(EVENT_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getEventNames() {
        return eventNames;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(SLOTS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getSlots() {
        return tags;
    }

    @JsonProperty(TAGS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getTags() {
        return tags;
    }
}
