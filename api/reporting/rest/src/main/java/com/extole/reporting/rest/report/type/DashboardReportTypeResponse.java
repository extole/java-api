package com.extole.reporting.rest.report.type;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportType;
import com.extole.reporting.rest.report.ReportTypeColumnResponse;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class DashboardReportTypeResponse extends ReportTypeResponse {

    @JsonCreator
    public DashboardReportTypeResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_EXECUTOR_TYPE) ReportExecutorType executorType,
        @JsonProperty(JSON_CATEGORIES) List<String> categories,
        @JsonProperty(JSON_SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(JSON_VISIBILITY) ReportTypeVisibility visibility,
        @JsonProperty(JSON_PARAMETERS) List<ReportTypeParameterDetailsResponse> parameters,
        @JsonProperty(JSON_FORMATS) List<ReportFormat> formats,
        @JsonProperty(JSON_ALLOWED_SCOPES) Set<ReportTypeScope> allowedScopes,
        @JsonProperty(JSON_PREVIEW_COLUMNS) List<ReportTypeColumnResponse> previewColumns,
        @JsonProperty(JSON_DATA_START) ZonedDateTime dataStart,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagResponse>> tags) {
        super(ReportType.DASHBOARD, name, displayName, description, executorType, categories, scopes, visibility,
            parameters, formats, allowedScopes, previewColumns, dataStart, tags);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportTypeResponse.Builder {

        private Builder() {
        }

        public DashboardReportTypeResponse build() {
            return new DashboardReportTypeResponse(name, displayName, description, executorType, categories, scopes,
                visibility, parameters, formats, allowedScopes, previewColumns, dataStart, tags);
        }
    }
}
