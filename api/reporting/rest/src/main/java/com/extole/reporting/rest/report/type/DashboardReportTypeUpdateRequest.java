package com.extole.reporting.rest.report.type;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportType;

public class DashboardReportTypeUpdateRequest extends ReportTypeUpdateRequest {

    public DashboardReportTypeUpdateRequest(@JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagRequest>> tags) {
        super(ReportType.DASHBOARD, tags);
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

        public DashboardReportTypeUpdateRequest build() {
            return new DashboardReportTypeUpdateRequest(tags);
        }
    }
}
