package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerLegacyQualityConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String ACTION_TYPE = "action_type";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> actionType;

    public CampaignControllerTriggerLegacyQualityConfiguration(
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
        @JsonProperty(ACTION_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerActionType> actionType,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.LEGACY_QUALITY,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.actionType = actionType;
    }

    @JsonProperty(ACTION_TYPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> getActionType() {
        return actionType;
    }

}
