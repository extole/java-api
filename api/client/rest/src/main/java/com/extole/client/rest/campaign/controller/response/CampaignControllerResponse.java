package com.extole.client.rest.campaign.controller.response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.StepScope;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerSelectorType;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerResponse extends CampaignStepResponse {

    public static final String STEP_TYPE_CONTROLLER = "CONTROLLER";

    private static final String JSON_CONTROLLER_ID = "controller_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_ENABLED_ON_STATES = "enabled_on_states";
    private static final String JSON_SELECTORS = "selectors";
    private static final String JSON_ACTIONS = "actions";
    private static final String JSON_ALIASES = "aliases";
    private static final String JSON_JOURNEY_NAMES = "journey_names";
    private static final String JSON_SEND_POLICY = "send_policy";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> enabledOnStates;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, List<CampaignControllerSelectorType>> selectors;
    private final List<CampaignControllerActionResponse> actions;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy;

    @JsonCreator
    public CampaignControllerResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_TRIGGERS) List<CampaignControllerTriggerResponse> triggers,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_SCOPE) BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope,
        @JsonProperty(JSON_ENABLED_ON_STATES) BuildtimeEvaluatable<CampaignBuildtimeContext,
            Set<CampaignState>> enabledOnStates,
        @JsonProperty(JSON_SELECTORS) BuildtimeEvaluatable<CampaignBuildtimeContext,
            List<CampaignControllerSelectorType>> selectors,
        @JsonProperty(JSON_ACTIONS) List<CampaignControllerActionResponse> actions,
        @JsonProperty(JSON_ALIASES) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> aliases,
        @JsonProperty(JSON_DATA) List<StepDataResponse> data,
        @JsonProperty(JSON_JOURNEY_NAMES) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames,
        @JsonProperty(JSON_SEND_POLICY) BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy) {
        super(id, enabled, triggers, componentIds, componentReferences, createdDate, updatedDate, data);
        this.name = name;
        this.scope = scope;
        this.enabledOnStates = enabledOnStates;
        this.selectors = selectors;
        this.actions = actions != null ? ImmutableList.copyOf(actions) : ImmutableList.of();
        this.aliases = aliases;
        this.journeyNames = journeyNames;
        this.sendPolicy = sendPolicy;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.CONTROLLER;
    }

    @Deprecated // TODO to get rid after UI will stop using it in logic ENG-21273
    @JsonProperty(JSON_CONTROLLER_ID)
    private String getControllerId() {
        return getId();
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
    public List<CampaignControllerActionResponse> getActions() {
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

    @JsonProperty(JSON_SEND_POLICY)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> getSendPolicy() {
        return sendPolicy;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
