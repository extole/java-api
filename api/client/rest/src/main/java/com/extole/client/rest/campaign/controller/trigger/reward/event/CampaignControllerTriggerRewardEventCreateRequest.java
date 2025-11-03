package com.extole.client.rest.campaign.controller.trigger.reward.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerRewardEventCreateRequest extends CampaignControllerTriggerRequest {

    private static final String REWARD_STATES = "reward_states";
    private static final String EVENT_NAMES = "event_names";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags;

    public CampaignControllerTriggerRewardEventCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(REWARD_STATES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates,
        @JsonProperty(EVENT_NAMES) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames,
        @JsonProperty(SLOTS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> slots,
        @JsonProperty(TAGS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated,
            componentIds, componentReferences);
        this.rewardStates = rewardStates;
        this.eventNames = eventNames;
        if (tags.isOmitted() || !Evaluatable.isDefined(tags.getValue())) {
            this.tags = slots;
        } else {
            this.tags = tags;
        }
    }

    @JsonProperty(REWARD_STATES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(EVENT_NAMES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getEventNames() {
        return eventNames;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(SLOTS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getSlots() {
        return tags;
    }

    @JsonProperty(TAGS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getTags() {
        return tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withRewardStates(Set<RewardState> eventTypes) {
            this.rewardStates = Omissible.of(Provided.of(eventTypes));
            return this;
        }

        public Builder withEventNames(Set<String> eventNames) {
            this.eventNames = Omissible.of(Provided.of(eventNames));
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(Provided.of(tags));
            return this;
        }

        public Builder withRewardStates(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> eventTypes) {
            this.rewardStates = Omissible.of(eventTypes);
            return this;
        }

        public Builder withEventNames(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames) {
            this.eventNames = Omissible.of(eventNames);
            return this;
        }

        public Builder withTags(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignControllerTriggerRewardEventCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerRewardEventCreateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                rewardStates,
                eventNames,
                tags,
                tags,
                componentIds,
                componentReferences);
        }

    }

}
