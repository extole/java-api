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

public class ConfiguredReportTypeResponse extends ReportTypeResponse {
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";

    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public ConfiguredReportTypeResponse(
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
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagResponse>> tags) {
        super(ReportType.CONFIGURED, name, displayName, description, executorType, categories, scopes, visibility,
            parameters, formats, allowedScopes, previewColumns, dataStart, tags);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportTypeResponse.Builder {
        private ZonedDateTime createdDate;
        private ZonedDateTime updateDate;

        private Builder() {
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdateDate(ZonedDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public ConfiguredReportTypeResponse build() {
            return new ConfiguredReportTypeResponse(name, displayName, description, executorType, categories, scopes,
                visibility, parameters, formats, allowedScopes, previewColumns, dataStart, createdDate, updateDate,
                tags);
        }
    }
}
