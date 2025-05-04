package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.send.reward.event.RewardState;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerSendRewardEventResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String REWARD_STATES = "reward_states";
    private static final String REWARD_NAMES = "reward_names";
    private static final String TAGS = "tags";

    private final Set<RewardState> rewardStates;
    private final Set<String> rewardNames;
    private final Set<String> tags;

    public BuiltCampaignControllerTriggerSendRewardEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(REWARD_STATES) Set<RewardState> rewardStates,
        @JsonProperty(REWARD_NAMES) Set<String> rewardNames,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.SEND_REWARD_EVENT, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.rewardStates = ImmutableSet.copyOf(rewardStates);
        this.rewardNames = ImmutableSet.copyOf(rewardNames);
        this.tags = ImmutableSet.copyOf(tags);
    }

    @JsonProperty(REWARD_STATES)
    public Set<RewardState> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(REWARD_NAMES)
    public Set<String> getRewardNames() {
        return rewardNames;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

}
