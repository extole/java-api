package com.extole.reporting.rest.posthandler.condition;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.reporting.rest.posthandler.ConditionType;

public class MatchReportTagsPostHandlerConditionRequest extends ReportPostHandlerConditionRequest {
    static final String TYPE = "MATCH_REPORT_TAGS";

    private static final String JSON_REPORT_TAGS = "report_tags";

    private final Set<String> reportTags;

    @JsonCreator
    public MatchReportTagsPostHandlerConditionRequest(
        @JsonProperty(JSON_REPORT_TAGS) Set<String> reportTags) {
        super(ConditionType.MATCH_REPORT_TAGS);
        this.reportTags = ImmutableSet.copyOf(reportTags);
    }

    @JsonProperty(JSON_REPORT_TAGS)
    public Set<String> getReportTags() {
        return reportTags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Set<String> tags = new HashSet<>();

        private Builder() {
        }

        public Builder withReportTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public MatchReportTagsPostHandlerConditionRequest build() {
            return new MatchReportTagsPostHandlerConditionRequest(tags);
        }
    }
}
