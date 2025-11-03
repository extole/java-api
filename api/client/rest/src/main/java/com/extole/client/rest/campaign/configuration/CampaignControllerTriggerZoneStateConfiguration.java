package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerZoneStateConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String ZONE_NAME = "zone_name";
    private static final String STEP_NAME = "step_name";
    private static final String INVERT_MAPPING_STATE = "invert_mapping_state";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> zoneName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> stepName;
    private final boolean invertMappingState;

    public CampaignControllerTriggerZoneStateConfiguration(
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
        @JsonProperty(ZONE_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> zoneName,
        @JsonProperty(STEP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> stepName,
        @JsonProperty(INVERT_MAPPING_STATE) boolean invertMappingState,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.ZONE_STATE,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
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
