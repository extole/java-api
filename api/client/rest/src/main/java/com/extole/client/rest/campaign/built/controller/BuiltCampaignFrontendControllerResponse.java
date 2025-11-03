package com.extole.client.rest.campaign.built.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.StepScope;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.built.step.data.BuiltStepDataResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public final class BuiltCampaignFrontendControllerResponse extends BuiltCampaignStepResponse {

    public static final String STEP_TYPE_FRONTEND_CONTROLLER = "FRONTEND_CONTROLLER";

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_ENABLED_ON_STATES = "enabled_on_states";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_ACTIONS = "actions";
    private static final String JSON_ALIASES = "aliases";
    private static final String JSON_JOURNEY_NAMES = "journey_names";
    private static final String JSON_SEND_POLICY = "send_policy";

    private final String name;
    private final StepScope scope;
    private final Set<CampaignState> enabledOnStates;
    private final String category;
    private final List<BuiltCampaignControllerActionResponse> actions;
    private final List<String> aliases;
    private final Set<String> journeyNames;
    private final SendPolicy sendPolicy;

    @JsonCreator
    public BuiltCampaignFrontendControllerResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_CONTROLLER_ENABLED) boolean enabled,
        @JsonProperty(JSON_TRIGGERS) List<BuiltCampaignControllerTriggerResponse> triggers,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_SCOPE) StepScope scope,
        @JsonProperty(JSON_ENABLED_ON_STATES) Set<CampaignState> enabledOnStates,
        @JsonProperty(JSON_CATEGORY) String category,
        @JsonProperty(JSON_ACTIONS) List<BuiltCampaignControllerActionResponse> actions,
        @JsonProperty(JSON_ALIASES) List<String> aliases,
        @JsonProperty(JSON_DATA) List<BuiltStepDataResponse> data,
        @JsonProperty(JSON_JOURNEY_NAMES) Set<String> journeyNames,
        @JsonProperty(JSON_SEND_POLICY) SendPolicy sendPolicy) {
        super(id, enabled, triggers, componentIds, componentReferences, createdDate, updatedDate, data);
        this.name = name;
        this.scope = scope;
        this.enabledOnStates = enabledOnStates != null ? ImmutableSet.copyOf(enabledOnStates) : ImmutableSet.of();
        this.category = category;
        this.actions =
            actions != null ? ImmutableList.copyOf(actions) : ImmutableList.of();
        this.aliases = aliases != null ? ImmutableList.copyOf(aliases) : ImmutableList.of();
        this.journeyNames = journeyNames != null ? ImmutableSet.copyOf(journeyNames) : ImmutableSet.of();
        this.sendPolicy = sendPolicy;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.FRONTEND_CONTROLLER;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPE)
    public StepScope getScope() {
        return scope;
    }

    @JsonProperty(JSON_ENABLED_ON_STATES)
    public Set<CampaignState> getEnabledOnStates() {
        return enabledOnStates;
    }

    @JsonProperty(JSON_CATEGORY)
    public String getCategory() {
        return category;
    }

    @JsonProperty(JSON_ACTIONS)
    public List<BuiltCampaignControllerActionResponse> getActions() {
        return actions;
    }

    @JsonProperty(JSON_ALIASES)
    public List<String> getAliases() {
        return aliases;
    }

    @JsonProperty(JSON_JOURNEY_NAMES)
    public Set<String> getJourneyNames() {
        return journeyNames;
    }

    @JsonProperty(JSON_SEND_POLICY)
    public SendPolicy getSendPolicy() {
        return sendPolicy;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
