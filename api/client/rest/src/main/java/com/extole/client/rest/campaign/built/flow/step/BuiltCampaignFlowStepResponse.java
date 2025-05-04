package com.extole.client.rest.campaign.built.flow.step;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltCampaignFlowStepResponse extends ComponentElementResponse {

    private static final String JSON_FLOW_STEP_ID = "flow_step_id";
    private static final String JSON_FLOW_PATH = "flow_path";
    private static final String JSON_SEQUENCE = "sequence";
    private static final String JSON_STEP_NAME = "step_name";
    private static final String JSON_ICON_TYPE = "icon_type";
    private static final String JSON_METRICS = "metrics";
    private static final String JSON_APPS = "apps";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_NAME = "name";
    private static final String JSON_ICON_COLOR = "icon_color";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_WORDS = "words";

    private final String flowStepId;
    private final String flowPath;
    private final BigDecimal sequence;
    private final String stepName;
    private final String iconType;
    private final List<BuiltCampaignFlowStepMetricResponse> metrics;
    private final List<BuiltCampaignFlowStepAppResponse> apps;
    private final Set<String> tags;
    private final String name;
    private final String iconColor;
    private final Optional<String> description;
    private final BuiltCampaignFlowStepWordsResponse words;

    @JsonCreator
    public BuiltCampaignFlowStepResponse(
        @JsonProperty(JSON_FLOW_STEP_ID) String flowStepId,
        @JsonProperty(JSON_FLOW_PATH) String flowPath,
        @JsonProperty(JSON_SEQUENCE) BigDecimal sequence,
        @JsonProperty(JSON_STEP_NAME) String stepName,
        @JsonProperty(JSON_ICON_TYPE) String iconType,
        @JsonProperty(JSON_METRICS) List<BuiltCampaignFlowStepMetricResponse> metrics,
        @JsonProperty(JSON_APPS) List<BuiltCampaignFlowStepAppResponse> apps,
        @JsonProperty(JSON_TAGS) Set<String> tags,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ICON_COLOR) String iconColor,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_WORDS) BuiltCampaignFlowStepWordsResponse words,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.flowStepId = flowStepId;
        this.flowPath = flowPath;
        this.sequence = sequence;
        this.stepName = stepName;
        this.iconType = iconType;
        this.metrics = metrics != null ? ImmutableList.copyOf(metrics) : ImmutableList.of();
        this.apps = apps != null ? ImmutableList.copyOf(apps) : ImmutableList.of();
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.of();
        this.name = name;
        this.iconColor = iconColor;
        this.description = description;
        this.words = words;
    }

    @JsonProperty(JSON_FLOW_STEP_ID)
    public String getFlowStepId() {
        return flowStepId;
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
    public List<BuiltCampaignFlowStepMetricResponse> getMetrics() {
        return metrics;
    }

    @JsonProperty(JSON_APPS)
    public List<BuiltCampaignFlowStepAppResponse> getApps() {
        return apps;
    }

    @JsonProperty(JSON_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_ICON_COLOR)
    public String getIconColor() {
        return iconColor;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_WORDS)
    public BuiltCampaignFlowStepWordsResponse getWords() {
        return words;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
