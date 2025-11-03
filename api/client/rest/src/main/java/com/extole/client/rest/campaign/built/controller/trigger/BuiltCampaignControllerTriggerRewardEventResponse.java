package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.reward.event.RewardState;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerRewardEventResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String REWARD_STATES = "reward_states";
    private static final String EVENT_NAMES = "event_names";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";

    private final Set<RewardState> rewardStates;
    private final Set<String> eventNames;
    private final Set<String> tags;

    public BuiltCampaignControllerTriggerRewardEventResponse(@JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(REWARD_STATES) Set<RewardState> rewardStates,
        @JsonProperty(EVENT_NAMES) Set<String> eventNames,
        @JsonProperty(SLOTS) Set<String> slots,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.REWARD_EVENT,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);
        this.rewardStates = rewardStates;
        this.eventNames = eventNames;
        this.tags = tags != null ? tags : slots;
    }

    @JsonProperty(REWARD_STATES)
    public Set<RewardState> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(EVENT_NAMES)
    public Set<String> getEventNames() {
        return eventNames;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(SLOTS)
    public Set<String> getSlots() {
        return tags;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }
}
