package com.extole.client.rest.campaign.flow.step.metric;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignFlowStepMetricUpdateRequest extends ComponentElementRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_UNIT = "unit";
    private static final String JSON_TAGS = "tags";

    private final Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> expression;
    private final Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> unit;
    private final Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>>> tags;

    @JsonCreator
    public CampaignFlowStepMetricUpdateRequest(
        @JsonProperty(JSON_NAME) Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> name,
        @JsonProperty(JSON_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_EXPRESSION) Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> expression,
        @JsonProperty(JSON_UNIT) Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> unit,
        @JsonProperty(JSON_TAGS) Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.unit = unit;
        this.tags = tags;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_EXPRESSION)
    public Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> getExpression() {
        return expression;
    }

    @JsonProperty(JSON_UNIT)
    public Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> getUnit() {
        return unit;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> expression = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String>> unit = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder
            withDescription(BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withExpression(BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> expression) {
            this.expression = Omissible.of(expression);
            return this;
        }

        public Builder withUnit(BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> unit) {
            this.unit = Omissible.of(unit);
            return this;
        }

        public Builder withTags(BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignFlowStepMetricUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignFlowStepMetricUpdateRequest(name,
                description,
                expression,
                unit,
                tags,
                componentIds,
                componentReferences);
        }

    }

}
