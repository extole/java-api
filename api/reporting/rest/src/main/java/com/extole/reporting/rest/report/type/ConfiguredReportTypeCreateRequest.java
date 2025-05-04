package com.extole.reporting.rest.report.type;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportType;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class ConfiguredReportTypeCreateRequest extends ReportTypeCreateRequest {

    private static final String JSON_PARENT_REPORT_TYPE_ID = "parent_report_type_id";
    private static final String JSON_PARAMETERS = "parameters";

    private final String parentReportTypeId;
    private final Optional<List<ReportTypeParameterDetailsRequest>> parameters;

    public ConfiguredReportTypeCreateRequest(
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_CATEGORIES) Optional<List<String>> categories,
        @JsonProperty(JSON_SCOPES) Optional<Set<ReportTypeScope>> scopes,
        @JsonProperty(JSON_VISIBILITY) Optional<ReportTypeVisibility> visibility,
        @JsonProperty(JSON_PARAMETERS) Optional<List<ReportTypeParameterDetailsRequest>> parameters,
        @JsonProperty(JSON_FORMATS) Optional<List<ReportFormat>> formats,
        @JsonProperty(JSON_ALLOWED_SCOPES) Optional<Set<ReportTypeScope>> allowedScopes,
        @JsonProperty(JSON_DATA_START) Optional<Instant> dataStart,
        @JsonProperty(JSON_PARENT_REPORT_TYPE_ID) String parentReportTypeId,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagRequest>> tags) {
        super(ReportType.CONFIGURED, displayName, description, categories, scopes, visibility, formats, allowedScopes,
            dataStart, tags);
        this.parentReportTypeId = parentReportTypeId;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_PARENT_REPORT_TYPE_ID)
    public String getParentReportTypeId() {
        return parentReportTypeId;
    }

    @JsonProperty(JSON_PARAMETERS)
    public Optional<List<ReportTypeParameterDetailsRequest>> getParameters() {
        return parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportTypeCreateRequest.Builder<Builder> {
        private String parentReportTypeId;
        private Optional<List<ReportTypeParameterDetailsRequest>> parameters = Optional.empty();

        private Builder() {
        }

        public Builder withParentReportTypeId(String parentReportTypeId) {
            this.parentReportTypeId = parentReportTypeId;
            return this;
        }

        public Builder withParameters(List<ReportTypeParameterDetailsRequest> parameters) {
            this.parameters = Optional.ofNullable(parameters);
            return this;
        }

        public ConfiguredReportTypeCreateRequest build() {
            return new ConfiguredReportTypeCreateRequest(displayName, description, categories, scopes, visibility,
                parameters, formats, allowedScopes, dataStart, parentReportTypeId, tags);
        }
    }
}
