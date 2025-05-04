package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.legacy.quality.CampaignControllerTriggerActionType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerLegacyQualityResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String ACTION_TYPE = "action_type";

    private final CampaignControllerTriggerActionType actionType;

    public BuiltCampaignControllerTriggerLegacyQualityResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(ACTION_TYPE) CampaignControllerTriggerActionType actionType,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.LEGACY_QUALITY, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.actionType = actionType;
    }

    @JsonProperty(ACTION_TYPE)
    public CampaignControllerTriggerActionType getActionType() {
        return actionType;
    }

}
