package com.extole.client.rest.campaign.flow.step;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppCreateRequest;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricCreateRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepCreateRequest extends ComponentElementRequest {

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

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType;
    private final List<CampaignFlowStepMetricCreateRequest> metrics;

    @Deprecated // TODO Remove after that UI will be adjusted ENG-18842
    private final List<CampaignFlowStepAppCreateRequest> apps;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description;
    private final Omissible<CampaignFlowStepWordsRequest> words;

    @JsonCreator
    public CampaignFlowStepCreateRequest(
        @JsonProperty(JSON_FLOW_PATH) BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath,
        @JsonProperty(JSON_SEQUENCE) BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence,
        @JsonProperty(JSON_STEP_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName,
        @JsonProperty(JSON_ICON_TYPE) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType,
        @JsonProperty(JSON_METRICS) List<CampaignFlowStepMetricCreateRequest> metrics,
        @JsonProperty(JSON_APPS) List<CampaignFlowStepAppCreateRequest> apps,
        @JsonProperty(JSON_TAGS) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags,
        @Nullable @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @Nullable @JsonProperty(JSON_ICON_COLOR) BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor,
        @JsonProperty(JSON_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_WORDS) Omissible<CampaignFlowStepWordsRequest> words,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
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
    public List<CampaignFlowStepMetricCreateRequest> getMetrics() {
        return metrics;
    }

    @Deprecated // TODO Remove after that UI will be adjusted ENG-18842
    @JsonProperty(JSON_APPS)
    public List<CampaignFlowStepAppCreateRequest> getApps() {
        return apps;
    }

    @JsonProperty(JSON_TAGS)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getTags() {
        return tags;
    }

    @Nullable
    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @Nullable
    @JsonProperty(JSON_ICON_COLOR)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getIconColor() {
        return iconColor;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_WORDS)
    public Omissible<CampaignFlowStepWordsRequest> getWords() {
        return words;
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType;
        private final List<CampaignFlowStepMetricCreateRequest.Builder> metricBuilders = Lists.newArrayList();
        private final List<CampaignFlowStepAppCreateRequest.Builder> appBuilders = Lists.newArrayList();
        // TODO ENG-16308 CampaignFlowStep requests should use omissible for name , iconColor, tags.
        private BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor;
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<CampaignFlowStepWordsRequest> words = Omissible.omitted();

        private Builder() {
        }

        public Builder withFlowPath(BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath) {
            this.flowPath = flowPath;
            return this;
        }

        public Builder withSequence(BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder withStepName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName) {
            this.stepName = stepName;
            return this;
        }

        public Builder withIconType(BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType) {
            this.iconType = iconType;
            return this;
        }

        public CampaignFlowStepMetricCreateRequest.Builder addMetric() {
            CampaignFlowStepMetricCreateRequest.Builder builder = CampaignFlowStepMetricCreateRequest.builder();
            metricBuilders.add(builder);
            return builder;
        }

        public CampaignFlowStepAppCreateRequest.Builder addApp() {
            CampaignFlowStepAppCreateRequest.Builder builder = CampaignFlowStepAppCreateRequest.builder();
            appBuilders.add(builder);
            return builder;
        }

        public Builder withTags(BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder withIconColor(BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor) {
            this.iconColor = iconColor;
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withWords(CampaignFlowStepWordsRequest words) {
            this.words = Omissible.of(words);
            return this;
        }

        public CampaignFlowStepCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignFlowStepCreateRequest(flowPath,
                sequence,
                stepName,
                iconType,
                metricBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()),
                appBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()),
                tags,
                name,
                iconColor,
                description,
                words,
                componentIds,
                componentReferences);
        }

    }

}
