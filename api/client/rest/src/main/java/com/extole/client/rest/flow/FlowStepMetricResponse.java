package com.extole.client.rest.flow;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public class FlowStepMetricResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_EXPRESSION = "expression";
    private static final String JSON_UNIT = "unit";
    private static final String JSON_TAGS = "tags";

    private final String name;
    private final String description;
    private final String expression;
    private final String unit;
    private final Set<String> tags;

    @JsonCreator
    public FlowStepMetricResponse(
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_EXPRESSION) String expression,
        @JsonProperty(JSON_UNIT) String unit,
        @JsonProperty(JSON_TAGS) Set<String> tags) {
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.unit = unit;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.of();
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(JSON_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_EXPRESSION)
    public String getExpression() {
        return expression;
    }

    @JsonProperty(JSON_UNIT)
    public String getUnit() {
        return unit;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
