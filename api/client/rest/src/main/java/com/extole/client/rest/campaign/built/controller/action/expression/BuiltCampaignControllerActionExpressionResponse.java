package com.extole.client.rest.campaign.built.controller.action.expression;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.step.action.expression.ExpressionActionCommandContext;
import com.extole.api.step.action.expression.ExpressionActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionExpressionResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_DATA = "data";

    private final RuntimeEvaluatable<ExpressionActionCommandContext, Void> expression;
    private final Map<String, RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>> data;

    public BuiltCampaignControllerActionExpressionResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_EXPRESSION) RuntimeEvaluatable<ExpressionActionCommandContext, Void> expression,
        @JsonProperty(JSON_DATA) Map<String, RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>> data) {
        super(actionId, CampaignControllerActionType.EXPRESSION, quality, enabled, componentIds, componentReferences);
        this.expression = expression;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_EXPRESSION)
    public RuntimeEvaluatable<ExpressionActionCommandContext, Void> getJsonExpression() {
        return expression;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>> getData() {
        return data;
    }

}
