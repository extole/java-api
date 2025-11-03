package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.trigger.expression.ExpressionTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerExpressionResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String DATA = "data";
    private static final String EXPRESSION = "expression";

    private final Map<String, String> data;
    private final RuntimeEvaluatable<ExpressionTriggerContext, Boolean> expression;

    public BuiltCampaignControllerTriggerExpressionResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(EXPRESSION) RuntimeEvaluatable<ExpressionTriggerContext, Boolean> expression,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.EXPRESSION,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);
        this.data = ImmutableMap.copyOf(data);
        this.expression = expression;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(EXPRESSION)
    public RuntimeEvaluatable<ExpressionTriggerContext, Boolean> getExpression() {
        return expression;
    }

}
