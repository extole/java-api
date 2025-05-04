package com.extole.client.rest.campaign.controller.trigger.send.reward.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerTriggerSendRewardEventUpdateRequest extends ComponentElementRequest {

    private static final String TRIGGER_PHASE = "trigger_phase";
    private static final String TRIGGER_NAME = "trigger_name";
    private static final String TRIGGER_DESCRIPTION = "trigger_description";
    private static final String ENABLED = "enabled";
    private static final String NEGATED = "negated";
    private static final String REWARD_STATES = "reward_states";
    private static final String REWARD_NAMES = "reward_names";
    private static final String TAGS = "tags";

    private final Omissible<
        BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags;

    @JsonCreator
    private CampaignControllerTriggerSendRewardEventUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(REWARD_STATES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates,
        @JsonProperty(REWARD_NAMES) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames,
        @JsonProperty(TAGS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
        this.rewardStates = rewardStates;
        this.rewardNames = rewardNames;
        this.tags = tags;
    }

    @JsonProperty(TRIGGER_PHASE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>>
        getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getNegated() {
        return negated;
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

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<RewardState>>> rewardStates =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> rewardNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withTriggerPhase(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase) {
            this.triggerPhase = Omissible.of(triggerPhase);
            return this;
        }

        public Builder withName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withNegated(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated) {
            this.negated = Omissible.of(negated);
            return this;
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

        public CampaignControllerTriggerSendRewardEventUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerSendRewardEventUpdateRequest(triggerPhase,
                name,
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
