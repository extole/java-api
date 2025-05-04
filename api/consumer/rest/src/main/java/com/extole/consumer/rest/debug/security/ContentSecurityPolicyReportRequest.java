package com.extole.consumer.rest.debug.security;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.debug.security.report.ReportRequest;

public class ContentSecurityPolicyReportRequest {

    private static final String JSON_CSP_REPORT = "csp-report";

    private final ReportRequest report;

    @JsonCreator
    public ContentSecurityPolicyReportRequest(@Nullable @JsonProperty(JSON_CSP_REPORT) ReportRequest report) {
        this.report = report;
    }

    @JsonProperty(JSON_CSP_REPORT)
    public ReportRequest getReport() {
        return report;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ReportRequest report;

        public Builder withReport(ReportRequest report) {
            this.report = report;
            return this;
        }

        public ContentSecurityPolicyReportRequest build() {
            return new ContentSecurityPolicyReportRequest(report);
        }
    }
}
