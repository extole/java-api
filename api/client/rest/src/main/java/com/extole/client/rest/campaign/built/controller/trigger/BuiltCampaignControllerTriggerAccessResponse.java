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
import com.extole.client.rest.client.Scope;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerAccessResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String TRUSTED_SCOPES = "trusted_scopes";

    private final Set<Scope> trustedScopes;

    public BuiltCampaignControllerTriggerAccessResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(TRUSTED_SCOPES) Set<Scope> trustedScopes,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.ACCESS,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);
        this.trustedScopes = ImmutableSet.copyOf(trustedScopes);
    }

    @JsonProperty(TRUSTED_SCOPES)
    public Set<Scope> getTrustedScopes() {
        return trustedScopes;
    }

}
