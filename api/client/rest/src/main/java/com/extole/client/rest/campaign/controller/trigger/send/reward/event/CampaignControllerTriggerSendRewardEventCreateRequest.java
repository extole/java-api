package com.extole.client.rest.campaign.controller.trigger.send.reward.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerTriggerSendRewardEventCreateRequest extends CampaignControllerTriggerRequest {

    private static final String REWARD_STATES = "reward_states";
    private static final String REWARD_NAMES = "reward_names";
    private static final String TAGS = "tags";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags;

    @JsonCreator
    private CampaignControllerTriggerSendRewardEventCreateRequest(
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
        @JsonProperty(REWARD_NAMES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames,
        @JsonProperty(TAGS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.rewardStates = rewardStates;
        this.rewardNames = rewardNames;
        this.tags = tags;
    }

    @JsonProperty(REWARD_STATES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> getRewardStates() {
        return rewardStates;
    }

    @JsonProperty(REWARD_NAMES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getRewardNames() {
        return rewardNames;
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
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder
            withRewardStates(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>> rewardStates) {
            this.rewardStates = Omissible.of(rewardStates);
            return this;
        }

        public Builder withRewardNames(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> rewardNames) {
            this.rewardNames = Omissible.of(rewardNames);
            return this;
        }

        public Builder withTags(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignControllerTriggerSendRewardEventCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerSendRewardEventCreateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                rewardStates,
                rewardNames,
                tags,
                componentIds,
                componentReferences);
        }

    }

}
