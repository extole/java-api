package com.extole.client.rest.campaign.controller.trigger.expression;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.trigger.expression.ExpressionTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerExpressionResponse extends CampaignControllerTriggerResponse {

    private static final String DATA = "data";
    private static final String EXPRESSION = "expression";

    private final Map<String, String> data;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression;

    public CampaignControllerTriggerExpressionResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @Nullable @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.EXPRESSION, triggerPhase, name, description, enabled, negated,
            componentIds, componentReferences);
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
