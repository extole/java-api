package com.extole.client.rest.campaign.controller.action.expression;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.expression.ExpressionActionCommandContext;
import com.extole.api.step.action.expression.ExpressionActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionExpressionResponse extends CampaignControllerActionResponse {

    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_DATA = "data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionActionCommandContext, Void>> expression;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>> data;

    public CampaignControllerActionExpressionResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionActionCommandContext, Void>> expression,
        @JsonProperty(JSON_DATA) Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>> data) {
        super(actionId, CampaignControllerActionType.EXPRESSION, quality, enabled, componentIds, componentReferences);
        this.expression = expression;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_EXPRESSION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ExpressionActionCommandContext, Void>>
        getExpression() {
        return expression;
    }

    @JsonProperty(JSON_DATA)
    public
        Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

}
