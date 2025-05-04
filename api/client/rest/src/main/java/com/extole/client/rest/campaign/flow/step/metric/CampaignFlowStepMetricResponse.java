package com.extole.client.rest.campaign.flow.step.metric;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepMetricResponse extends ComponentElementResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_UNIT = "unit";
    private static final String JSON_TAGS = "tags";

    private final String id;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>> description;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> expression;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> unit;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> tags;

    @JsonCreator
    public CampaignFlowStepMetricResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_EXPRESSION) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> expression,
        @JsonProperty(JSON_UNIT) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> unit,
        @JsonProperty(JSON_TAGS) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.unit = unit;
        this.tags = tags;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_EXPRESSION)
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getExpression() {
        return expression;
    }

    @JsonProperty(JSON_UNIT)
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> getUnit() {
        return unit;
    }

    @JsonProperty(JSON_TAGS)
    public BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
