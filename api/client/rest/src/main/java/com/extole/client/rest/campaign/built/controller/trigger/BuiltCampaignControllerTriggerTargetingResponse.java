package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.targeting.TargetingScope;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerTargetingResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String SCOPE = "scope";

    private final TargetingScope scope;

    public BuiltCampaignControllerTriggerTargetingResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(SCOPE) TargetingScope scope,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.TARGETING,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);

        this.scope = scope;
    }

    @JsonProperty(SCOPE)
    public TargetingScope getScope() {
        return scope;
    }

}
