package com.extole.client.rest.campaign.controller.trigger.zone.state;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerZoneStateResponse extends CampaignControllerTriggerResponse {

    private static final String ZONE_NAME = "zone_name";
    private static final String STEP_NAME = "step_name";
    private static final String INVERT_MAPPING_STATE = "invert_mapping_state";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> zoneName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> stepName;
    private final boolean invertMappingState;

    public CampaignControllerTriggerZoneStateResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(ZONE_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> zoneName,
        @JsonProperty(STEP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> stepName,
        @JsonProperty(INVERT_MAPPING_STATE) boolean invertMappingState,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.ZONE_STATE, triggerPhase, name, description, enabled, negated,
            componentIds, componentReferences);
        this.zoneName = zoneName;
        this.stepName = stepName;
        this.invertMappingState = invertMappingState;
    }

    @JsonProperty(ZONE_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> getZoneName() {
        return zoneName;
    }

    @JsonProperty(STEP_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> getStepName() {
        return stepName;
    }

    @JsonProperty(INVERT_MAPPING_STATE)
    public boolean isInvertMappingState() {
        return invertMappingState;
    }

}
