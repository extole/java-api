package com.extole.reporting.rest.report;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.extole.reporting.rest.report.execution.ReportFormat;

public class ReportTypeV4Response {
    private static final String JSON_NAME = "name";
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_CATEGORIES = "categories";
    private static final String JSON_EXECUTOR_TYPE = "executor_type";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_PARAMETERS = "parameters";
    private static final String JSON_FORMATS = "formats";
    private static final String JSON_ALLOWED_SCOPES = "allowed_scopes";
    private static final String JSON_PREVIEW_COLUMNS = "preview_columns";
    private static final String JSON_DATA_START = "data_start";

    private final String name;
    private final String displayName;
    private final String description;
    private final List<String> categories;
    private final ReportExecutorType executorType;
    private final Set<ReportTypeScope> scopes;
    private final List<ReportTypeParameterDetailsResponse> parameters;
    private final List<ReportFormat> formats;
    private final Set<ReportTypeScope> allowedScopes;
    private final List<ReportTypeColumnResponse> previewColumns;
    private final ZonedDateTime dataStart;

    public ReportTypeV4Response(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_CATEGORIES) List<String> categories,
        @JsonProperty(JSON_EXECUTOR_TYPE) ReportExecutorType executorType,
        @JsonProperty(JSON_SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(JSON_PARAMETERS) List<ReportTypeParameterDetailsResponse> parameters,
        @JsonProperty(JSON_FORMATS) List<ReportFormat> formats,
        @JsonProperty(JSON_ALLOWED_SCOPES) Set<ReportTypeScope> allowedScopes,
        @JsonProperty(JSON_PREVIEW_COLUMNS) List<ReportTypeColumnResponse> previewColumns,
        @JsonProperty(JSON_DATA_START) ZonedDateTime dataStart) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.categories = categories != null ? categories : Collections.emptyList();
        this.executorType = executorType;
        this.scopes = scopes != null ? scopes : Collections.emptySet();
        this.parameters = parameters != null ? parameters : Collections.emptyList();
        this.formats = formats != null ? formats : Collections.emptyList();
        this.allowedScopes = allowedScopes != null ? allowedScopes : Collections.emptySet();
        this.previewColumns = previewColumns != null ? ImmutableList.copyOf(previewColumns) : ImmutableList.of();
        this.dataStart = dataStart;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_CATEGORIES)
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_EXECUTOR_TYPE)
    public ReportExecutorType getExecutorType() {
        return executorType;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_PARAMETERS)
    public List<ReportTypeParameterDetailsResponse> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_ALLOWED_SCOPES)
    public Set<ReportTypeScope> getAllowedScopes() {
        return allowedScopes;
    }

    @JsonProperty(JSON_PREVIEW_COLUMNS)
    public List<ReportTypeColumnResponse> getPreviewColumns() {
        return previewColumns;
    }

    @JsonProperty(JSON_DATA_START)
    public ZonedDateTime getDataStart() {
        return dataStart;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String displayName;
        private String description;
        private final List<String> categories = Lists.newArrayList();
        private ReportExecutorType executorType;
        private final Set<ReportTypeScope> scopes = Sets.newHashSet();
        private final List<ReportTypeParameterDetailsResponse> parameters = Lists.newArrayList();
        private final List<ReportFormat> formats = Lists.newArrayList();
        private final Set<ReportTypeScope> allowedScopes = Sets.newHashSet();
        private List<ReportTypeColumnResponse> previewColumns = Lists.newArrayList();
        private ZonedDateTime dataStart;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCategories(List<String> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
            return this;
        }

        public Builder withExecutionType(ReportExecutorType executionType) {
            this.executorType = executionType;
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes.clear();
            this.scopes.addAll(scopes);
            return this;
        }

        public Builder withParameters(List<ReportTypeParameterDetailsResponse> parameters) {
            this.parameters.clear();
            this.parameters.addAll(parameters);
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats.clear();
            this.formats.addAll(formats);
            return this;
        }

        public Builder withAllowedScopes(Set<ReportTypeScope> allowedScopes) {
            this.allowedScopes.clear();
            this.allowedScopes.addAll(allowedScopes);
            return this;
        }

        public Builder withPreviewColumns(List<ReportTypeColumnResponse> previewColumns) {
            this.previewColumns = ImmutableList.copyOf(previewColumns);
            return this;
        }

        public Builder withDataStart(ZonedDateTime dataStart) {
            this.dataStart = dataStart;
            return this;
        }

        public ReportTypeV4Response build() {
            return new ReportTypeV4Response(name, displayName, description, categories, executorType, scopes,
                parameters,
                formats, allowedScopes, previewColumns, dataStart);
        }
    }
}
