package com.extole.client.rest.campaign.controller.action.expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.expression.ExpressionActionCommandContext;
import com.extole.api.step.action.expression.ExpressionActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionExpressionUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionActionCommandContext, Void>>> expression;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>>> data;

    @JsonCreator
    public CampaignControllerActionExpressionUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_EXPRESSION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionActionCommandContext, Void>>> expression,
        @JsonProperty(JSON_DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.expression = expression;
        this.data = data;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_EXPRESSION)
    public
        Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ExpressionActionCommandContext, Void>>>
        getExpression() {
        return expression;
    }

    @JsonProperty(JSON_DATA)
    public
        Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>>>
        getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ExpressionActionCommandContext, Void>>> expression =
                Omissible.omitted();
        private Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>>> data =
                    Omissible.omitted();

        private Builder() {
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withExpression(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionActionCommandContext, Void>> expression) {
            this.expression = Omissible.of(expression);
            return this;
        }

        public Builder withData(
            Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ExpressionActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder clearData() {
            this.data = Omissible.of(Collections.emptyMap());
            return this;
        }

        public CampaignControllerActionExpressionUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionExpressionUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                expression,
                data);
        }

    }

}
