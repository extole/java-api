package com.extole.reporting.rest.posthandler.condition;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.reporting.rest.posthandler.ConditionType;

public class MatchReportTagsPostHandlerConditionResponse extends ReportPostHandlerConditionResponse {
    static final String TYPE = "MATCH_REPORT_TAGS";

    private static final String JSON_REPORT_TAGS = "report_tags";

    private final Set<String> reportTags;

    @JsonCreator
    public MatchReportTagsPostHandlerConditionResponse(@JsonProperty(JSON_ID) String conditionId,
        @JsonProperty(JSON_REPORT_TAGS) Set<String> reportTags) {
        super(conditionId, ConditionType.MATCH_REPORT_TAGS);
        this.reportTags = ImmutableSet.copyOf(reportTags);
    }

    @JsonProperty(JSON_REPORT_TAGS)
    public Set<String> getReportTags() {
        return reportTags;
    }

}
