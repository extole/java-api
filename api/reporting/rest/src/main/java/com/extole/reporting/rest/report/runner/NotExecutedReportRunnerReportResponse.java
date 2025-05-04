package com.extole.reporting.rest.report.runner;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportParameterResponse;

public class NotExecutedReportRunnerReportResponse extends BaseReportRunnerReportResponse {
    public static final String TYPE = "NOT_EXECUTED";
    private static final String SLOT = "slot";
    private final String slot;

    @JsonCreator
    public NotExecutedReportRunnerReportResponse(
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(DISPLAY_NAME) String displayName,
        @JsonProperty(PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(SLOT) String slot) {
        super(TYPE, name, reportType, displayName, parameters, tags);
        this.slot = slot;
    }

    @JsonProperty(SLOT)
    public String getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends BaseReportRunnerReportResponse.Builder {
        private String slot;

        public Builder withSlot(String slot) {
            this.slot = slot;
            return this;
        }

        public NotExecutedReportRunnerReportResponse build() {
            return new NotExecutedReportRunnerReportResponse(reportType, reportType, displayName, parameters, tags,
                slot);
        }
    }
}
