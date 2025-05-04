package com.extole.client.rest.campaign.flow.step;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignFlowStepUpdateRequest extends ComponentElementRequest {

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

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> flowPath;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal>> sequence;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> stepName;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconType;
    private final Omissible<List<CampaignFlowStepMetricCreateRequest>> metrics;
    private final Omissible<List<CampaignFlowStepAppCreateRequest>> apps;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> tags;
    // TODO ENG-16308 CampaignFlowStep requests should use omissible for name , iconColor, tags.
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconColor;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description;
    private final Omissible<CampaignFlowStepWordsRequest> words;

    @JsonCreator
    public CampaignFlowStepUpdateRequest(
        @JsonProperty(JSON_FLOW_PATH) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> flowPath,
        @JsonProperty(JSON_SEQUENCE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal>> sequence,
        @JsonProperty(JSON_STEP_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> stepName,
        @JsonProperty(JSON_ICON_TYPE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconType,
        @JsonProperty(JSON_METRICS) Omissible<List<CampaignFlowStepMetricCreateRequest>> metrics,
        @JsonProperty(JSON_APPS) Omissible<List<CampaignFlowStepAppCreateRequest>> apps,
        @JsonProperty(JSON_TAGS) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> tags,
        @JsonProperty(JSON_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name,
        @JsonProperty(JSON_ICON_COLOR) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconColor,
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
        this.metrics = metrics;
        this.apps = apps;
        this.tags = tags;
        this.name = name;
        this.iconColor = iconColor;
        this.description = description;
        this.words = words;
    }

    @JsonProperty(JSON_FLOW_PATH)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getFlowPath() {
        return flowPath;
    }

    @JsonProperty(JSON_SEQUENCE)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal>> getSequence() {
        return sequence;
    }

    @JsonProperty(JSON_STEP_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getStepName() {
        return stepName;
    }

    @JsonProperty(JSON_ICON_TYPE)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getIconType() {
        return iconType;
    }

    @JsonProperty(JSON_METRICS)
    public Omissible<List<CampaignFlowStepMetricCreateRequest>> getMetrics() {
        return metrics;
    }

    @JsonProperty(JSON_APPS)
    public Omissible<List<CampaignFlowStepAppCreateRequest>> getApps() {
        return apps;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(JSON_ICON_COLOR)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getIconColor() {
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

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> flowPath = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal>> sequence = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> stepName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconType = Omissible.omitted();
        private List<CampaignFlowStepMetricCreateRequest.Builder> metricBuilders;
        private List<CampaignFlowStepAppCreateRequest.Builder> appBuilders;
        private Omissible<List<CampaignFlowStepMetricCreateRequest>> metrics = Omissible.omitted();
        private Omissible<List<CampaignFlowStepAppCreateRequest>> apps = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>>> tags = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> iconColor = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<CampaignFlowStepWordsRequest> words = Omissible.omitted();

        private Builder() {
        }

        public Builder withFlowPath(BuildtimeEvaluatable<CampaignBuildtimeContext, String> flowPath) {
            this.flowPath = Omissible.of(flowPath);
            return this;
        }

        public Builder withSequence(BuildtimeEvaluatable<CampaignBuildtimeContext, BigDecimal> sequence) {
            this.sequence = Omissible.of(sequence);
            return this;
        }

        public Builder withStepName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> stepName) {
            this.stepName = Omissible.of(stepName);
            return this;
        }

        public Builder withIconType(BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconType) {
            this.iconType = Omissible.of(iconType);
            return this;
        }

        public Builder withNoMetrics() {
            this.metricBuilders = Collections.emptyList();
            return this;
        }

        public CampaignFlowStepMetricCreateRequest.Builder addMetric() {
            if (metricBuilders == null) {
                metricBuilders = Lists.newArrayList();
            }

            CampaignFlowStepMetricCreateRequest.Builder builder = CampaignFlowStepMetricCreateRequest.builder();
            metricBuilders.add(builder);
            return builder;
        }

        public Builder withNullMetrics() {
            metrics = Omissible.nullified();
            return this;
        }

        public Builder withNoApps() {
            this.appBuilders = Collections.emptyList();
            return this;
        }

        public CampaignFlowStepAppCreateRequest.Builder addApp() {
            if (appBuilders == null) {
                appBuilders = Lists.newArrayList();
            }

            CampaignFlowStepAppCreateRequest.Builder builder = CampaignFlowStepAppCreateRequest.builder();
            appBuilders.add(builder);
            return builder;
        }

        public Builder withNullApps() {
            apps = Omissible.nullified();
            return this;
        }

        public Builder withTags(Provided<CampaignBuildtimeContext, Set<String>> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withNoTags() {
            this.tags = Omissible.of(Provided.emptySet());
            return this;
        }

        public Builder withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withIconColor(BuildtimeEvaluatable<CampaignBuildtimeContext, String> iconColor) {
            this.iconColor = Omissible.of(iconColor);
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

        public CampaignFlowStepUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignFlowStepUpdateRequest(flowPath,
                sequence,
                stepName,
                iconType,
                buildMetricRequests(),
                buildAppsRequests(),
                tags,
                name,
                iconColor,
                description,
                words,
                componentIds,
                componentReferences);
        }

        private Omissible<List<CampaignFlowStepAppCreateRequest>> buildAppsRequests() {
            return appBuilders == null ? apps
                : Omissible.of(appBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
        }

        private Omissible<List<CampaignFlowStepMetricCreateRequest>> buildMetricRequests() {
            return metricBuilders == null ? metrics
                : Omissible.of(metricBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
        }

    }

}
