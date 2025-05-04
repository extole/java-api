package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerConfiguration extends CampaignStepConfiguration {

    public static final String STEP_TYPE_CONTROLLER = "CONTROLLER";

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_ENABLED_ON_STATES = "enabled_on_states";
    private static final String JSON_SELECTORS = "selectors";
    private static final String JSON_ACTIONS = "actions";
    private static final String JSON_ALIASES = "aliases";
    private static final String JSON_JOURNEY_NAMES = "journey_names";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> enabledOnStates;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>> selectors;
    private final List<CampaignControllerActionConfiguration> actions;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames;

    @JsonCreator
    public CampaignControllerConfiguration(
        @JsonProperty(JSON_ID) Omissible<Id<CampaignStepConfiguration>> id,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_TRIGGERS) List<CampaignControllerTriggerConfiguration> triggers,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_SCOPE) BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope,
        @JsonProperty(JSON_ENABLED_ON_STATES) BuildtimeEvaluatable<CampaignBuildtimeContext,
            Set<CampaignState>> enabledOnStates,
        @JsonProperty(JSON_SELECTORS) BuildtimeEvaluatable<CampaignBuildtimeContext,
            List<CampaignControllerSelectorType>> selectors,
        @JsonProperty(JSON_ACTIONS) List<CampaignControllerActionConfiguration> actions,
        @JsonProperty(JSON_ALIASES) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases,
        @JsonProperty(JSON_DATA) List<StepDataConfiguration> data,
        @JsonProperty(JSON_JOURNEY_NAMES) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames) {
        super(id, enabled, triggers, componentReferences, data);
        this.name = name;
        this.scope = scope;
        this.enabledOnStates = enabledOnStates;
        this.selectors = selectors;
        this.actions = actions != null ? unmodifiableList(actions) : Collections.emptyList();
        this.aliases = aliases;
        this.journeyNames = journeyNames;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.CONTROLLER;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_ENABLED_ON_STATES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> getEnabledOnStates() {
        return enabledOnStates;
    }

    @JsonProperty(JSON_SELECTORS)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>> getSelectors() {
        return selectors;
    }

    @JsonProperty(JSON_ACTIONS)
    public List<CampaignControllerActionConfiguration> getActions() {
        return actions;
    }

    @JsonProperty(JSON_ALIASES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getAliases() {
        return aliases;
    }

    @JsonProperty(JSON_JOURNEY_NAMES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getJourneyNames() {
        return journeyNames;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
