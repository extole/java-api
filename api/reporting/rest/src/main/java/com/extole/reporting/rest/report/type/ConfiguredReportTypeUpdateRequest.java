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

public class ConfiguredReportTypeUpdateRequest extends ReportTypeUpdateRequest {
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_CATEGORIES = "categories";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_VISIBILITY = "visibility";
    private static final String JSON_FORMATS = "formats";
    private static final String JSON_ALLOWED_SCOPES = "allowed_scopes";
    private static final String JSON_DATA_START = "data_start";
    private static final String JSON_PARAMETERS = "parameters";

    private final Optional<String> displayName;
    private final Optional<String> description;
    private final Optional<List<String>> categories;
    private final Optional<Set<ReportTypeScope>> scopes;
    private final Optional<ReportTypeVisibility> visibility;
    private final Optional<List<ReportFormat>> formats;
    private final Optional<Set<ReportTypeScope>> allowedScopes;
    private final Optional<Instant> dataStart;
    private final Optional<List<ReportTypeParameterDetailsRequest>> parameters;

    public ConfiguredReportTypeUpdateRequest(
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_CATEGORIES) Optional<List<String>> categories,
        @JsonProperty(JSON_SCOPES) Optional<Set<ReportTypeScope>> scopes,
        @JsonProperty(JSON_VISIBILITY) Optional<ReportTypeVisibility> visibility,
        @JsonProperty(JSON_PARAMETERS) Optional<List<ReportTypeParameterDetailsRequest>> parameters,
        @JsonProperty(JSON_FORMATS) Optional<List<ReportFormat>> formats,
        @JsonProperty(JSON_ALLOWED_SCOPES) Optional<Set<ReportTypeScope>> allowedScopes,
        @JsonProperty(JSON_DATA_START) Optional<Instant> dataStart,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagRequest>> tags) {
        super(ReportType.CONFIGURED, tags);
        this.displayName = displayName;
        this.description = description;
        this.categories = categories;
        this.scopes = scopes;
        this.visibility = visibility;
        this.formats = formats;
        this.allowedScopes = allowedScopes;
        this.dataStart = dataStart;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_CATEGORIES)
    public Optional<List<String>> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_SCOPES)
    public Optional<Set<ReportTypeScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_VISIBILITY)
    public Optional<ReportTypeVisibility> getVisibility() {
        return visibility;
    }

    @JsonProperty(JSON_FORMATS)
    public Optional<List<ReportFormat>> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_ALLOWED_SCOPES)
    public Optional<Set<ReportTypeScope>> getAllowedScopes() {
        return allowedScopes;
    }

    @JsonProperty(JSON_DATA_START)
    public Optional<Instant> getDataStart() {
        return dataStart;
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

    public static final class Builder extends ReportTypeUpdateRequest.Builder<Builder> {
        private Optional<List<ReportTypeParameterDetailsRequest>> parameters = Optional.empty();

        private Builder() {
        }

        public Builder withParameters(List<ReportTypeParameterDetailsRequest> parameters) {
            this.parameters = Optional.ofNullable(parameters);
            return this;
        }

        public ConfiguredReportTypeUpdateRequest build() {
            return new ConfiguredReportTypeUpdateRequest(displayName, description, categories, scopes, visibility,
                parameters, formats, allowedScopes, dataStart, tags);
        }
    }
}
