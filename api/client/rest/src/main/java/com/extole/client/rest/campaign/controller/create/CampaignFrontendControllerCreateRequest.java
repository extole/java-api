package com.extole.client.rest.campaign.controller.create;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.StepScope;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public final class CampaignFrontendControllerCreateRequest extends CampaignStepCreateRequest {

    public static final String STEP_TYPE_FRONTEND_CONTROLLER = "FRONTEND_CONTROLLER";

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_ENABLED_ON_STATES = "enabled_on_states";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_ALIASES = "aliases";
    private static final String JSON_JOURNEY_NAMES = "journey_names";
    private static final String JSON_SEND_POLICY = "send_policy";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> category;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy;

    @JsonCreator
    public CampaignFrontendControllerCreateRequest(
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_SCOPE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope,
        @JsonProperty(JSON_ENABLED_ON_STATES) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates,
        @JsonProperty(JSON_CATEGORY) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> category,
        @JsonProperty(JSON_ALIASES) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases,
        @JsonProperty(JSON_JOURNEY_NAMES) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames,
        @JsonProperty(JSON_SEND_POLICY) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy) {
        super(enabled, componentIds, componentReferences);
        this.name = name;
        this.scope = scope;
        this.enabledOnStates = enabledOnStates;
        this.category = category;
        this.aliases = aliases;
        this.journeyNames = journeyNames;
        this.sendPolicy = sendPolicy;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.FRONTEND_CONTROLLER;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> getScope() {
        return scope;
    }

    @JsonProperty(JSON_ENABLED_ON_STATES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> getEnabledOnStates() {
        return enabledOnStates;
    }

    @JsonProperty(JSON_CATEGORY)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getCategory() {
        return category;
    }

    @JsonProperty(JSON_ALIASES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> getAliases() {
        return aliases;
    }

    @JsonProperty(JSON_JOURNEY_NAMES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> getJourneyNames() {
        return journeyNames;
    }

    @JsonProperty(JSON_SEND_POLICY)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> getSendPolicy() {
        return sendPolicy;
    }

    public static Builder builder(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
        return new Builder(name);
    }

    public static final class Builder
        extends CampaignStepCreateRequest.Builder<Builder, CampaignFrontendControllerCreateRequest> {

        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> category = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy = Omissible.omitted();

        private Builder(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
        }

        public Builder withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder withScope(BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder
            withEnabledOnStates(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> enabledOnStates) {
            this.enabledOnStates = Omissible.of(enabledOnStates);
            return this;
        }

        public Builder withCategory(BuildtimeEvaluatable<CampaignBuildtimeContext, String> category) {
            this.category = Omissible.of(category);
            return this;
        }

        public Builder withEnabledOnStates(Set<CampaignState> enabledOnStates) {
            this.enabledOnStates = Omissible.of(Provided.of(enabledOnStates));
            return this;
        }

        public Builder withCategory(String category) {
            this.category = Omissible.of(Provided.of(category));
            return this;
        }

        public Builder withAliases(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases) {
            this.aliases = Omissible.of(aliases);
            return this;
        }

        public Builder withJourneyNames(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames) {
            this.journeyNames = Omissible.of(journeyNames);
            return this;
        }

        public Builder withJourneyNames(Set<String> journeyNames) {
            this.journeyNames = Omissible.of(Provided.of(ImmutableSet.copyOf(journeyNames)));
            return this;
        }

        public Builder withJourneyName(String journeyName) {
            this.journeyNames = Omissible.of(Provided.setOf(journeyName));
            return this;
        }

        public Builder withSendPolicy(BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy) {
            this.sendPolicy = Omissible.of(sendPolicy);
            return this;
        }

        @Override
        public CampaignFrontendControllerCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignFrontendControllerCreateRequest(
                enabled,
                componentIds,
                componentReferences,
                name,
                scope,
                enabledOnStates,
                category,
                aliases,
                journeyNames,
                sendPolicy);
        }

    }

}
