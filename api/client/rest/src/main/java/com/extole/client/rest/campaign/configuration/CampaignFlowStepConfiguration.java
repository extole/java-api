package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepConfiguration {

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
    private static final String JSON_COMPONENT_REFERENCES = "component_references";

    private final Omissible<Id<CampaignFlowStepConfiguration>> flowStepId;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType;
    private final List<CampaignFlowStepMetricConfiguration> metrics;
    private final List<CampaignFlowStepAppConfiguration> apps;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description;
    private final CampaignFlowStepWordsConfiguration words;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;

    @JsonCreator
    public CampaignFlowStepConfiguration(
        @JsonProperty(JSON_FLOW_STEP_ID) Omissible<Id<CampaignFlowStepConfiguration>> flowStepId,
        @JsonProperty(JSON_FLOW_PATH) BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath,
        @JsonProperty(JSON_SEQUENCE) BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence,
        @JsonProperty(JSON_STEP_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName,
        @JsonProperty(JSON_ICON_TYPE) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType,
        @JsonProperty(JSON_METRICS) List<CampaignFlowStepMetricConfiguration> metrics,
        @JsonProperty(JSON_APPS) List<CampaignFlowStepAppConfiguration> apps,
        @JsonProperty(JSON_TAGS) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_ICON_COLOR) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_WORDS) CampaignFlowStepWordsConfiguration words,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
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
        this.componentReferences =
            componentReferences != null ? unmodifiableList(componentReferences) : Collections.emptyList();
    }

    @JsonProperty(JSON_FLOW_STEP_ID)
    public Omissible<Id<CampaignFlowStepConfiguration>> getFlowStepId() {
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
    public List<CampaignFlowStepMetricConfiguration> getMetrics() {
        return metrics;
    }

    @JsonProperty(JSON_APPS)
    public List<CampaignFlowStepAppConfiguration> getApps() {
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
    public CampaignFlowStepWordsConfiguration getWords() {
        return words;
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
