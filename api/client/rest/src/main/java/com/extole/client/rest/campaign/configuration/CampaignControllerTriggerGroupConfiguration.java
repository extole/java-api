package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.controller.trigger.group.Operator;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerGroupConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String OPERATOR = "operator";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Operator> operator;

    public CampaignControllerTriggerGroupConfiguration(
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
        @JsonProperty(OPERATOR) BuildtimeEvaluatable<ControllerBuildtimeContext, Operator> operator,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.GROUP,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.operator = operator;
    }

    @JsonProperty(OPERATOR)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Operator> getOperator() {
        return operator;
    }

}
