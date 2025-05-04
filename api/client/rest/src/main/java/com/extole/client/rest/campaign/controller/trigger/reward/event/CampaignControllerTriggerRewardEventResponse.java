package com.extole.client.rest.campaign.controller.trigger.reward.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerRewardEventResponse extends CampaignControllerTriggerResponse {

    private static final String REWARD_STATES = "reward_states";
    private static final String EVENT_NAMES = "event_names";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags;

    public CampaignControllerTriggerRewardEventResponse(@JsonProperty(TRIGGER_ID) String triggerId,
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
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.REWARD_EVENT, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
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
