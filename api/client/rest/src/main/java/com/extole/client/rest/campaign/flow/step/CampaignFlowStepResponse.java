package com.extole.client.rest.campaign.flow.step;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppResponse;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepResponse extends ComponentElementResponse {

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
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType;
    private final List<CampaignFlowStepMetricResponse> metrics;
    private final List<CampaignFlowStepAppResponse> apps;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description;
    private final CampaignFlowStepWordsResponse words;

    @JsonCreator
    public CampaignFlowStepResponse(
        @JsonProperty(JSON_FLOW_STEP_ID) String flowStepId,
        @JsonProperty(JSON_FLOW_PATH) BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath,
        @JsonProperty(JSON_SEQUENCE) BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence,
        @JsonProperty(JSON_STEP_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName,
        @JsonProperty(JSON_ICON_TYPE) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType,
        @JsonProperty(JSON_METRICS) List<CampaignFlowStepMetricResponse> metrics,
        @JsonProperty(JSON_APPS) List<CampaignFlowStepAppResponse> apps,
        @JsonProperty(JSON_TAGS) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_ICON_COLOR) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_WORDS) CampaignFlowStepWordsResponse words,
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
        this.tags = tags;
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
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getFlowPath() {
        return flowPath;
    }

    @JsonProperty(JSON_SEQUENCE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> getSequence() {
        return sequence;
    }

    @JsonProperty(JSON_STEP_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getStepName() {
        return stepName;
    }

    @JsonProperty(JSON_ICON_TYPE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconType() {
        return iconType;
    }

    @JsonProperty(JSON_METRICS)
    public List<CampaignFlowStepMetricResponse> getMetrics() {
        return metrics;
    }

    @JsonProperty(JSON_APPS)
    public List<CampaignFlowStepAppResponse> getApps() {
        return apps;
    }

    @JsonProperty(JSON_TAGS)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_ICON_COLOR)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconColor() {
        return iconColor;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_WORDS)
    public CampaignFlowStepWordsResponse getWords() {
        return words;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
