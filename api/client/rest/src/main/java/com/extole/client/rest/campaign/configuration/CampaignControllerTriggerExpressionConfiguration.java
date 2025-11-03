package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.trigger.expression.ExpressionTriggerContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerExpressionConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String DATA = "data";
    private static final String EXPRESSION = "expression";

    private final Map<String, String> data;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression;

    public CampaignControllerTriggerExpressionConfiguration(
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
        @Nullable @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.EXPRESSION,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.data = data == null ? null : ImmutableMap.copyOf(data);
        this.expression = expression;
    }

    @JsonProperty(DATA)
    @Nullable
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(EXPRESSION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ExpressionTriggerContext, Boolean>>
        getExpression() {
        return expression;
    }

}
