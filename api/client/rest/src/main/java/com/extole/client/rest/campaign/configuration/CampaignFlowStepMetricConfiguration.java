package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepMetricConfiguration {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_UNIT = "unit";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_COMPONENT_REFERENCES = "component_references";

    private final Omissible<Id<CampaignFlowStepMetricConfiguration>> id;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Optional<String>> description;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> expression;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> unit;
    private final BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> tags;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;

    @JsonCreator
    public CampaignFlowStepMetricConfiguration(
        @JsonProperty(JSON_ID) Omissible<Id<CampaignFlowStepMetricConfiguration>> id,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> name,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_EXPRESSION) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> expression,
        @JsonProperty(JSON_UNIT) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, String> unit,
        @JsonProperty(JSON_TAGS) BuildtimeEvaluatable<AllFlowStepsBuildtimeContext, Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.unit = unit;
        this.tags = tags;
        this.componentReferences = componentReferences != null ? unmodifiableList(componentReferences) : List.of();
    }

    @JsonProperty(JSON_ID)
    public Omissible<Id<CampaignFlowStepMetricConfiguration>> getId() {
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

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
