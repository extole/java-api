package com.extole.client.rest.campaign.controller.trigger.expression;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.trigger.expression.ExpressionTriggerContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerExpressionRequest extends CampaignControllerTriggerRequest {
    private static final String DATA = "data";
    private static final String EXPRESSION = "expression";

    private final Map<String, String> data;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression;

    public CampaignControllerTriggerExpressionRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @Nullable @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(EXPRESSION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.data = data;
        this.expression = expression;
    }

    @Nullable
    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(EXPRESSION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ExpressionTriggerContext, Boolean>>
        getExpression() {
        return expression;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {
        private Map<String, String> data;
        private BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression;

        private Builder() {
        }

        public Builder withData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Builder withExpression(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionTriggerContext, Boolean>> expression) {
            this.expression = expression;
            return this;
        }

        @Override
        public CampaignControllerTriggerExpressionRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerExpressionRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                data,
                expression,
                componentIds,
                componentReferences);
        }

    }

}
