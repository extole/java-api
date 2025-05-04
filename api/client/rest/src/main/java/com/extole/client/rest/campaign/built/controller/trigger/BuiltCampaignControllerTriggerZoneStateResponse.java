package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerZoneStateResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String ZONE_NAME = "zone_name";
    private static final String STEP_NAME = "step_name";
    private static final String INVERT_MAPPING_STATE = "invert_mapping_state";

    private final String zoneName;
    private final String stepName;
    private final boolean invertMappingState;

    public BuiltCampaignControllerTriggerZoneStateResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(ZONE_NAME) String zoneName,
        @JsonProperty(STEP_NAME) String stepName,
        @JsonProperty(INVERT_MAPPING_STATE) boolean invertMappingState,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.ZONE_STATE, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.zoneName = zoneName;
        this.stepName = stepName;
        this.invertMappingState = invertMappingState;
    }

    @Nullable
    @JsonProperty(ZONE_NAME)
    public String getZoneName() {
        return zoneName;
    }

    @Nullable
    @JsonProperty(STEP_NAME)
    public String getStepName() {
        return stepName;
    }

    @JsonProperty(INVERT_MAPPING_STATE)
    public boolean isInvertMappingState() {
        return invertMappingState;
    }

}
