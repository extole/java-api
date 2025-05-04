package com.extole.client.rest.flow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;

public class FlowStepResponse {

    private static final String JSON_FLOW_PATH = "flow_path";
    private static final String JSON_SEQUENCE = "sequence";
    private static final String JSON_STEP_NAME = "step_name";
    private static final String JSON_ICON_TYPE = "icon_type";
    private static final String JSON_METRICS = "metrics";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_NAME = "name";
    private static final String JSON_ICON_COLOR = "icon_color";
    private static final String JSON_DATA = "data";
    private static final String JSON_TRIGGERS = "triggers";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_WORDS = "words";

    private final String flowPath;
    private final BigDecimal sequence;
    private final String stepName;
    private final String iconType;
    private final List<FlowStepMetricResponse> metrics;
    private final Set<String> tags;
    private final String name;
    private final String iconColor;
    private final List<FlowStepDataResponse> data;
    private final Set<InputFlowStepTriggerResponse> inputFlowStepTriggerResponses;
    private final Optional<String> description;
    private final FlowStepWordsResponse words;

    @JsonCreator
    public FlowStepResponse(@JsonProperty(JSON_FLOW_PATH) String flowPath,
        @JsonProperty(JSON_SEQUENCE) BigDecimal sequence,
        @JsonProperty(JSON_STEP_NAME) String stepName,
        @JsonProperty(JSON_ICON_TYPE) String iconType,
        @JsonProperty(JSON_METRICS) List<FlowStepMetricResponse> metrics,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ICON_COLOR) String iconColor,
        @JsonProperty(JSON_DATA) List<FlowStepDataResponse> data,
        @JsonProperty(JSON_TRIGGERS) Set<InputFlowStepTriggerResponse> inputFlowStepTriggerResponses,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_WORDS) FlowStepWordsResponse words) {
        this.flowPath = flowPath;
        this.sequence = sequence;
        this.stepName = stepName;
        this.iconType = iconType;
        this.metrics = ImmutableList.copyOf(metrics);
        this.tags = ImmutableSet.copyOf(tags);
        this.name = name;
        this.iconColor = iconColor;
        this.data = ImmutableList.copyOf(data);
        this.inputFlowStepTriggerResponses = ImmutableSet.copyOf(inputFlowStepTriggerResponses);
        this.description = description;
        this.words = words;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_FLOW_PATH)
    public String getFlowPath() {
        return flowPath;
    }

    @JsonProperty(JSON_SEQUENCE)
    public BigDecimal getSequence() {
        return sequence;
    }

    @JsonProperty(JSON_STEP_NAME)
    public String getStepName() {
        return stepName;
    }

    @JsonProperty(JSON_ICON_TYPE)
    public String getIconType() {
        return iconType;
    }

    @JsonProperty(JSON_METRICS)
    public List<FlowStepMetricResponse> getMetrics() {
        return metrics;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_ICON_COLOR)
    public String getIconColor() {
        return iconColor;
    }

    @JsonProperty(JSON_DATA)
    public List<FlowStepDataResponse> getData() {
        return data;
    }

    @JsonProperty(JSON_TRIGGERS)
    public Set<? extends InputFlowStepTriggerResponse> getTriggers() {
        return inputFlowStepTriggerResponses;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_WORDS)
    public FlowStepWordsResponse getWords() {
        return words;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
