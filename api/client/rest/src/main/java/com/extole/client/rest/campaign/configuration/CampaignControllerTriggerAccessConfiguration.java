package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerAccessConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String TRUSTED_SCOPES = "trusted_scopes";

    private final Set<Scope> trustedScopes;

    public CampaignControllerTriggerAccessConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(TRUSTED_SCOPES) Set<Scope> trustedScopes,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.ACCESS,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.trustedScopes = ImmutableSet.copyOf(trustedScopes);
    }

    @JsonProperty(TRUSTED_SCOPES)
    public Set<Scope> getTrustedScopes() {
        return trustedScopes;
    }

}
