package com.extole.client.rest.campaign.controller.update;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.StepScope;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerSelectorType;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.step.data.StepDataCreateRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerUpdateRequest extends CampaignStepUpdateRequest {

    public static final String STEP_TYPE_CONTROLLER = "CONTROLLER";

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_ENABLED_ON_STATES = "enabled_on_states";
    private static final String JSON_SELECTORS = "selectors";
    private static final String JSON_ALIASES = "aliases";
    private static final String JSON_DATA = "data";
    private static final String JSON_JOURNEY_NAMES = "journey_names";
    private static final String JSON_SEND_POLICY = "send_policy";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates;
    private final Omissible<
        BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>>> selectors;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases;
    @Deprecated // TODO Remove after that UI will be adjusted ENG-18927
    private final Omissible<List<StepDataCreateRequest>> data;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy;

    public CampaignControllerUpdateRequest(
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name,
        @JsonProperty(JSON_SCOPE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope,
        @JsonProperty(JSON_ENABLED_ON_STATES) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates,
        @JsonProperty(JSON_SELECTORS) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>>> selectors,
        @JsonProperty(JSON_ALIASES) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases,
        @JsonProperty(JSON_DATA) Omissible<List<StepDataCreateRequest>> data,
        @JsonProperty(JSON_JOURNEY_NAMES) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames,
        @JsonProperty(JSON_SEND_POLICY) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy) {
        super(enabled, componentIds, componentReferences);
        this.name = name;
        this.scope = scope;
        this.enabledOnStates = enabledOnStates;
        this.selectors = selectors;
        this.aliases = aliases;
        this.data = data;
        this.journeyNames = journeyNames;
        this.sendPolicy = sendPolicy;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.CONTROLLER;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getName() {
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

    @JsonProperty(JSON_SELECTORS)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>>>
        getSelectors() {
        return selectors;
    }

    @JsonProperty(JSON_ALIASES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> getAliases() {
        return aliases;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<List<StepDataCreateRequest>> getData() {
        return data;
    }

    @JsonProperty(JSON_JOURNEY_NAMES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> getJourneyNames() {
        return journeyNames;
    }

    @JsonProperty(JSON_SEND_POLICY)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> getSendPolicy() {
        return sendPolicy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends CampaignStepUpdateRequest.Builder<Builder, CampaignControllerUpdateRequest> {

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope>> scope = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>>> enabledOnStates =
            Omissible.omitted();
        private Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>>> selectors =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> aliases = Omissible.omitted();
        private List<StepDataCreateRequest.Builder<?>> dataBuilders;
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> journeyNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy>> sendPolicy = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = Omissible.of(Provided.of(name));
            return this;
        }

        public Builder withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withScope(BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder withEnabledOnStates(Set<CampaignState> enabledOnStates) {
            this.enabledOnStates = Omissible.of(Provided.of(enabledOnStates));
            return this;
        }

        public Builder
            withEnabledOnStates(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> enabledOnStates) {
            this.enabledOnStates = Omissible.of(enabledOnStates);
            return this;
        }

        public Builder withSelectors(List<CampaignControllerSelectorType> selectors) {
            this.selectors = Omissible.of(Provided.of(selectors));
            return this;
        }

        public Builder withSelectors(
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>> selectors) {
            this.selectors = Omissible.of(selectors);
            return this;
        }

        public Builder withAliases(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases) {
            this.aliases = Omissible.of(aliases);
            return this;
        }

        @Deprecated // TODO Remove after that UI will be adjusted ENG-18927
        public StepDataCreateRequest.Builder<Builder> addData() {
            if (dataBuilders == null) {
                dataBuilders = Lists.newArrayList();
            }
            StepDataCreateRequest.Builder<Builder> stepDataCreateRequestBuilder = StepDataCreateRequest.builder(this);
            this.dataBuilders.add(stepDataCreateRequestBuilder);
            return stepDataCreateRequestBuilder;
        }

        public Builder clearData() {
            dataBuilders = Collections.emptyList();
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

        public Builder withSendPolicy(
            BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy) {
            this.sendPolicy = Omissible.of(sendPolicy);
            return this;
        }

        @Override
        public CampaignControllerUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerUpdateRequest(
                enabled,
                componentIds,
                componentReferences,
                name,
                scope,
                enabledOnStates,
                selectors,
                aliases,
                dataBuilders == null ? Omissible.omitted()
                    : Omissible.of(dataBuilders.stream()
                        .map(dataBuilder -> dataBuilder.build())
                        .collect(Collectors.toList())),
                journeyNames, sendPolicy);
        }

    }

}
