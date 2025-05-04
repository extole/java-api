package com.extole.client.rest.flow;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class FlowQueryParams {

    @Deprecated // TODO remove after UI migration ENG-16210-1
    private static final String FLOW_PATHS = "flow_paths";
    private static final String FLOW_PATHS_TO_INCLUDE = "flow_paths_to_include";
    private static final String FLOW_PATHS_TO_EXCLUDE = "flow_paths_to_exclude";
    @Deprecated // TODO remove after UI migration ENG-16210-1
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String CAMPAIGN_IDS = "campaign_ids";
    @Deprecated // TODO remove after UI migration ENG-16210-1
    private static final String PROGRAM_LABEL = "program_label";
    private static final String PROGRAM_LABELS = "program_labels";
    private static final String STEPS_TO_INCLUDE = "steps_to_include";
    private static final String TAGS_TO_INCLUDE = "tags_to_include";
    private static final String METRIC_TAGS_TO_INCLUDE = "metric_tags_to_include";
    private static final String STEPS_TO_EXCLUDE = "steps_to_exclude";
    private static final String TAGS_TO_EXCLUDE = "tags_to_exclude";
    private static final String METRIC_TAGS_TO_EXCLUDE = "metric_tags_to_exclude";
    private static final String CAMPAIGN_VERSION_STATE = "campaign_version_state";
    private static final String FLOW_FILTER = "flow_filter";
    private static final String SIMPLE_TRIGGER = "simple_trigger";

    @Deprecated // TODO remove after UI migration ENG-16210-1
    private final Optional<Set<String>> flowPaths;
    private final Optional<Set<String>> flowPathsToInclude;
    private final Optional<Set<String>> flowPathsToExclude;
    @Deprecated // TODO remove after UI migration ENG-16210-1
    private final Optional<String> campaignId;
    private final Optional<Set<String>> campaignIds;
    @Deprecated // TODO remove after UI migration ENG-16210-1
    private final Optional<String> programLabel;
    private final Optional<Set<String>> programLabels;
    private final Optional<Set<String>> stepsToInclude;
    private final Optional<Set<String>> tagsToInclude;
    private final Optional<Set<String>> tagsToExclude;
    private final Optional<Set<String>> metricTagsToInclude;
    private final Optional<Set<String>> stepsToExclude;
    private final Optional<Set<String>> metricTagsToExclude;
    private final Optional<String> campaignVersionState;
    private final Optional<FlowFilter> flowFilter;
    private final Optional<Boolean> simpleTrigger;

    public FlowQueryParams(
        @Deprecated // TODO remove after UI migration ENG-16210-1
        @Nullable @QueryParam(FLOW_PATHS) Set<String> flowPaths,
        @Nullable @QueryParam(FLOW_PATHS_TO_INCLUDE) Set<String> flowPathsToInclude,
        @Nullable @QueryParam(FLOW_PATHS_TO_EXCLUDE) Set<String> flowPathsToExclude,
        @Deprecated // TODO remove after UI migration ENG-16210-1
        @Nullable @QueryParam(CAMPAIGN_ID) String campaignId,
        @Nullable @QueryParam(CAMPAIGN_IDS) Set<String> campaignIds,
        @Deprecated // TODO remove after UI migration ENG-16210-1
        @Nullable @QueryParam(PROGRAM_LABEL) String programLabel,
        @Nullable @QueryParam(PROGRAM_LABELS) Set<String> programLabels,
        @Nullable @QueryParam(STEPS_TO_INCLUDE) Set<String> stepsToInclude,
        @Nullable @QueryParam(TAGS_TO_INCLUDE) Set<String> tagsToInclude,
        @Nullable @QueryParam(METRIC_TAGS_TO_INCLUDE) Set<String> metricTagsToInclude,
        @Nullable @QueryParam(STEPS_TO_EXCLUDE) Set<String> stepsToExclude,
        @Nullable @QueryParam(TAGS_TO_EXCLUDE) Set<String> tagsToExclude,
        @Nullable @QueryParam(METRIC_TAGS_TO_EXCLUDE) Set<String> metricTagsToExclude,
        @Nullable @QueryParam(CAMPAIGN_VERSION_STATE) String campaignVersionState,
        @Nullable @QueryParam(FLOW_FILTER) FlowFilter flowFilter,
        @Nullable @QueryParam(SIMPLE_TRIGGER) Boolean simpleTrigger) {
        this.flowPaths = Optional.ofNullable(flowPaths);
        this.flowPathsToInclude = Optional.ofNullable(flowPathsToInclude);
        this.flowPathsToExclude = Optional.ofNullable(flowPathsToExclude);
        this.campaignId = Optional.ofNullable(campaignId);
        this.campaignIds = Optional.ofNullable(campaignIds);
        this.programLabel = Optional.ofNullable(programLabel);
        this.programLabels = Optional.ofNullable(programLabels);
        this.stepsToInclude = Optional.ofNullable(stepsToInclude);
        this.tagsToInclude = Optional.ofNullable(tagsToInclude);
        this.metricTagsToInclude = Optional.ofNullable(metricTagsToInclude);
        this.metricTagsToExclude = Optional.ofNullable(metricTagsToExclude);
        this.stepsToExclude = Optional.ofNullable(stepsToExclude);
        this.tagsToExclude = Optional.ofNullable(tagsToExclude);
        this.campaignVersionState = Optional.ofNullable(campaignVersionState);
        this.simpleTrigger = Optional.ofNullable(simpleTrigger);
        this.flowFilter = Optional.ofNullable(flowFilter);
    }

    @Deprecated // TODO remove after UI migration ENG-16210-1
    @QueryParam(FLOW_PATHS)
    public Optional<Set<String>> getFlowPaths() {
        return flowPaths;
    }

    @QueryParam(FLOW_PATHS_TO_INCLUDE)
    public Optional<Set<String>> getFlowPathsToInclude() {
        return flowPathsToInclude;
    }

    @QueryParam(FLOW_PATHS_TO_EXCLUDE)
    public Optional<Set<String>> getFlowPathsToExclude() {
        return flowPathsToExclude;
    }

    @Deprecated // TODO remove after UI migration ENG-16210-1
    @QueryParam(CAMPAIGN_ID)
    public Optional<String> getCampaignId() {
        return campaignId;
    }

    @QueryParam(CAMPAIGN_IDS)
    public Optional<Set<String>> getCampaignIds() {
        return campaignIds;
    }

    @Deprecated // TODO remove after UI migration ENG-16210-1
    @QueryParam(PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @QueryParam(PROGRAM_LABELS)
    public Optional<Set<String>> getProgramLabels() {
        return programLabels;
    }

    @QueryParam(STEPS_TO_INCLUDE)
    public Optional<Set<String>> getStepsToInclude() {
        return stepsToInclude;
    }

    @QueryParam(TAGS_TO_INCLUDE)
    public Optional<Set<String>> getTagsToInclude() {
        return tagsToInclude;
    }

    @QueryParam(TAGS_TO_EXCLUDE)
    public Optional<Set<String>> getTagsToExclude() {
        return tagsToExclude;
    }

    @QueryParam(METRIC_TAGS_TO_INCLUDE)
    public Optional<Set<String>> getMetricTagsToInclude() {
        return metricTagsToInclude;
    }

    @QueryParam(STEPS_TO_EXCLUDE)
    public Optional<Set<String>> getStepsToExclude() {
        return stepsToExclude;
    }

    @QueryParam(METRIC_TAGS_TO_EXCLUDE)
    public Optional<Set<String>> getMetricTagsToExclude() {
        return metricTagsToExclude;
    }

    @QueryParam(CAMPAIGN_VERSION_STATE)
    public Optional<String> getCampaignVersionState() {
        return campaignVersionState;
    }

    @QueryParam(FLOW_FILTER)
    public Optional<FlowFilter> getFlowFilter() {
        return flowFilter;
    }

    @QueryParam(SIMPLE_TRIGGER)
    public Optional<Boolean> getSimpleTrigger() {
        return simpleTrigger;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        @Deprecated // TODO remove after UI migration ENG-16210-1
        private Set<String> flowPaths;
        private Set<String> flowPathsToInclude;
        private Set<String> flowPathsToExclude;
        @Deprecated // TODO remove after UI migration ENG-16210-1
        private String campaignId;
        private Set<String> campaignIds;
        @Deprecated // TODO remove after UI migration ENG-16210-1
        private String programLabel;
        private Set<String> programLabels;
        private Set<String> stepsToInclude;
        private Set<String> tagsToInclude;
        private Set<String> tagsToExclude;
        private Set<String> metricTagsToInclude;
        private Set<String> stepsToExclude;
        private Set<String> metricTagsToExclude;
        private String campaignVersionState;
        private FlowFilter campaignState;
        private Boolean simpleTrigger;

        private Builder() {
        }

        @Deprecated // TODO remove after UI migration ENG-16210-1
        public Builder withFlowPaths(Set<String> flowPaths) {
            this.flowPaths = flowPaths;
            return this;
        }

        public Builder withFlowPathsToInclude(Set<String> flowPathsToInclude) {
            this.flowPathsToInclude = flowPathsToInclude;
            return this;
        }

        public Builder withFlowPathsToExclude(Set<String> flowPathsToExclude) {
            this.flowPathsToExclude = flowPathsToExclude;
            return this;
        }

        @Deprecated // TODO remove after UI migration ENG-16210-1
        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withCampaignIds(Set<String> campaignIds) {
            this.campaignIds = campaignIds;
            return this;
        }

        @Deprecated // TODO remove after UI migration ENG-16210-1
        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withProgramLabels(Set<String> programLabels) {
            this.programLabels = programLabels;
            return this;
        }

        public Builder withStepsToInclude(Set<String> stepsToInclude) {
            this.stepsToInclude = stepsToInclude;
            return this;
        }

        public Builder withTagsToInclude(Set<String> tagsToInclude) {
            this.tagsToInclude = tagsToInclude;
            return this;
        }

        public Builder withTagsToExclude(Set<String> tagsToExclude) {
            this.tagsToExclude = tagsToExclude;
            return this;
        }

        public Builder withMetricTagsToInclude(Set<String> metricTagsToInclude) {
            this.metricTagsToInclude = metricTagsToInclude;
            return this;
        }

        public Builder withStepsToExclude(Set<String> stepsToExclude) {
            this.stepsToExclude = stepsToExclude;
            return this;
        }

        public Builder withMetricTagsToExclude(Set<String> metricTagsToExclude) {
            this.metricTagsToExclude = metricTagsToExclude;
            return this;
        }

        public Builder withCampaignVersionState(String campaignVersionState) {
            this.campaignVersionState = campaignVersionState;
            return this;
        }

        public Builder withCampaignState(FlowFilter campaignState) {
            this.campaignState = campaignState;
            return this;
        }

        public Builder withSimpleTrigger(Boolean simpleTrigger) {
            this.simpleTrigger = simpleTrigger;
            return this;
        }

        public FlowQueryParams build() {
            return new FlowQueryParams(flowPaths, flowPathsToInclude, flowPathsToExclude, campaignId,
                campaignIds, programLabel, programLabels, stepsToInclude, tagsToInclude, metricTagsToInclude,
                stepsToExclude, tagsToExclude, metricTagsToExclude, campaignVersionState, campaignState, simpleTrigger);
        }
    }
}
