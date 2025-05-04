package com.extole.reporting.rest.report.type;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportType;

public class SparkReportTypeUpdateRequest extends ReportTypeUpdateRequest {

    public SparkReportTypeUpdateRequest(@JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagRequest>> tags) {
        super(ReportType.SPARK, tags);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportTypeUpdateRequest.Builder<Builder> {
        private Builder() {
        }

        public SparkReportTypeUpdateRequest build() {
            return new SparkReportTypeUpdateRequest(tags);
        }
    }
}
