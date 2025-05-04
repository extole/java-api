package com.extole.client.rest.campaign.summary;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.QueryParam;

import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.CampaignState;

public class CampaignSummaryListQueryParams {

    private static final String QUERY_PARAM_VERSION = "version";
    private static final String QUERY_PARAM_PROGRAM_TYPE = "program_type";
    private static final String QUERY_PARAM_TAGS = "tags";
    private static final String QUERY_PARAM_LABELS = "labels";
    private static final String QUERY_PARAM_PROGRAM_LABEL = "program_label";
    private static final String QUERY_PARAM_STATES = "states";
    private static final String QUERY_PARAM_INCLUDE_ARCHIVED = "include_archived";

    private final Optional<String> version;
    private final Optional<String> programType;
    private final Set<String> tags;
    private final Set<String> labels;
    private final Optional<String> programLabel;
    private final Set<CampaignState> states;
    private final Optional<Boolean> includeArchived;

    public CampaignSummaryListQueryParams(
        @QueryParam(QUERY_PARAM_VERSION) Optional<String> version,
        @QueryParam(QUERY_PARAM_PROGRAM_TYPE) Optional<String> programType,
        @Nullable @QueryParam(QUERY_PARAM_TAGS) Set<String> tags,
        @Nullable @QueryParam(QUERY_PARAM_LABELS) Set<String> labels,
        @QueryParam(QUERY_PARAM_PROGRAM_LABEL) Optional<String> programLabel,
        @Nullable @QueryParam(QUERY_PARAM_STATES) Set<CampaignState> states,
        @QueryParam(QUERY_PARAM_INCLUDE_ARCHIVED) Optional<Boolean> includeArchived) {
        this.version = version;
        this.programType = programType;
        this.tags = tags != null ? ImmutableSet.copyOf(tags) : Collections.emptySet();
        this.labels = labels != null ? ImmutableSet.copyOf(labels) : Collections.emptySet();
        this.programLabel = programLabel;
        this.states = states != null ? ImmutableSet.copyOf(states) : Collections.emptySet();
        this.includeArchived = includeArchived;
    }

    @QueryParam(QUERY_PARAM_VERSION)
    public Optional<String> getVersion() {
        return version;
    }

    @QueryParam(QUERY_PARAM_PROGRAM_TYPE)
    public Optional<String> getProgramType() {
        return programType;
    }

    @QueryParam(QUERY_PARAM_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @QueryParam(QUERY_PARAM_LABELS)
    public Set<String> getLabels() {
        return labels;
    }

    @QueryParam(QUERY_PARAM_PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @QueryParam(QUERY_PARAM_STATES)
    public Set<CampaignState> getStates() {
        return states;
    }

    @QueryParam(QUERY_PARAM_INCLUDE_ARCHIVED)
    public Optional<Boolean> getIncludeArchived() {
        return includeArchived;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> version = Optional.empty();
        private Optional<String> programType = Optional.empty();
        private Set<String> tags;
        private Set<String> labels;
        private Optional<String> programLabel = Optional.empty();

        private Set<CampaignState> states;
        private Optional<Boolean> includeArchived = Optional.empty();

        private Builder() {
        }

        public Builder withVersion(String version) {
            this.version = Optional.ofNullable(version);
            return this;
        }

        public Builder withProgramType(String programType) {
            this.programType = Optional.ofNullable(programType);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withLabels(Set<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = Optional.ofNullable(programLabel);
            return this;
        }

        public Builder withStates(Set<CampaignState> states) {
            this.states = states;
            return this;
        }

        public Builder withIncludeArchived(boolean includeArchived) {
            this.includeArchived = Optional.of(Boolean.valueOf(includeArchived));
            return this;
        }

        public CampaignSummaryListQueryParams build() {
            return new CampaignSummaryListQueryParams(version, programType, tags, labels, programLabel, states,
                includeArchived);
        }
    }
}
