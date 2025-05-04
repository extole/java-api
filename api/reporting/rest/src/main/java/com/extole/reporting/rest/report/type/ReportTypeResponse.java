package com.extole.reporting.rest.report.type;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportType;
import com.extole.reporting.rest.report.ReportTypeColumnResponse;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.execution.ReportFormat;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportTypeResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DashboardReportTypeResponse.class, name = "DASHBOARD"),
    @JsonSubTypes.Type(value = SparkReportTypeResponse.class, name = "SPARK"),
    @JsonSubTypes.Type(value = SqlReportTypeResponse.class, name = "SQL"),
    @JsonSubTypes.Type(value = ConfiguredReportTypeResponse.class, name = "CONFIGURED")
})
public abstract class ReportTypeResponse {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_NAME = "name";
    protected static final String JSON_DISPLAY_NAME = "display_name";
    protected static final String JSON_EXECUTOR_TYPE = "executor_type";
    protected static final String JSON_DESCRIPTION = "description";
    protected static final String JSON_CATEGORIES = "categories";
    protected static final String JSON_SCOPES = "scopes";
    protected static final String JSON_VISIBILITY = "visibility";
    protected static final String JSON_PARAMETERS = "parameters";
    protected static final String JSON_FORMATS = "formats";
    protected static final String JSON_ALLOWED_SCOPES = "allowed_scopes";
    protected static final String JSON_PREVIEW_COLUMNS = "preview_columns";
    protected static final String JSON_DATA_START = "data_start";
    protected static final String JSON_TAGS = "tags";

    private final ReportType type;
    private final String name;
    private final String displayName;
    private final ReportExecutorType executorType;
    private final String description;
    private final List<String> categories;
    private final Set<ReportTypeScope> scopes;
    private final ReportTypeVisibility visibility;
    private final List<ReportTypeParameterDetailsResponse> parameters;
    private final List<ReportFormat> formats;
    private final Set<ReportTypeScope> allowedScopes;
    private final List<ReportTypeColumnResponse> previewColumns;
    private final ZonedDateTime dataStart;
    private final Optional<Set<ReportTypeTagResponse>> tags;

    public ReportTypeResponse(
        ReportType type,
        String name,
        String displayName,
        String description,
        ReportExecutorType executorType,
        List<String> categories,
        Set<ReportTypeScope> scopes,
        ReportTypeVisibility visibility,
        List<ReportTypeParameterDetailsResponse> parameters,
        List<ReportFormat> formats,
        Set<ReportTypeScope> allowedScopes,
        List<ReportTypeColumnResponse> previewColumns,
        ZonedDateTime dataStart,
        Optional<Set<ReportTypeTagResponse>> tags) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.executorType = executorType;
        this.categories = categories;
        this.scopes = ImmutableSet.copyOf(scopes);
        this.visibility = visibility;
        this.parameters = ImmutableList.copyOf(parameters);
        this.formats = ImmutableList.copyOf(formats);
        this.allowedScopes = ImmutableSet.copyOf(allowedScopes);
        this.previewColumns = ImmutableList.copyOf(previewColumns);
        this.dataStart = dataStart;
        this.tags = tags;
    }

    @JsonProperty(JSON_TYPE)
    public ReportType getType() {
        return type;
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

    @JsonProperty(JSON_EXECUTOR_TYPE)
    public ReportExecutorType getExecutorType() {
        return executorType;
    }

    @JsonProperty(JSON_CATEGORIES)
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_VISIBILITY)
    public ReportTypeVisibility getVisibility() {
        return visibility;
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

    @JsonProperty(JSON_TAGS)
    public Optional<Set<ReportTypeTagResponse>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @SuppressWarnings("unchecked")
    public static class Builder {
        protected String name;
        protected String displayName;
        protected String description;
        protected ReportExecutorType executorType;
        protected List<String> categories;
        protected Set<ReportTypeScope> scopes;
        protected ReportTypeVisibility visibility;
        protected List<ReportTypeParameterDetailsResponse> parameters;
        protected List<ReportFormat> formats;
        protected Set<ReportTypeScope> allowedScopes;
        protected List<ReportTypeColumnResponse> previewColumns;
        protected ZonedDateTime dataStart;
        protected Optional<Set<ReportTypeTagResponse>> tags = Optional.empty();

        protected Builder() {
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

        public Builder withExecutorType(ReportExecutorType executorType) {
            this.executorType = executorType;
            return this;
        }

        public Builder withCategories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withVisibility(ReportTypeVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder withParameters(List<ReportTypeParameterDetailsResponse> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = formats;
            return this;
        }

        public Builder withAllowedScopes(Set<ReportTypeScope> allowedScopes) {
            this.allowedScopes = allowedScopes;
            return this;
        }

        public Builder withPreviewColumns(List<ReportTypeColumnResponse> previewColumns) {
            this.previewColumns = previewColumns;
            return this;
        }

        public Builder withDataStart(ZonedDateTime dataStart) {
            this.dataStart = dataStart;
            return this;
        }

        public Builder withTags(Set<ReportTypeTagResponse> tags) {
            this.tags = Optional.ofNullable(tags);
            return this;
        }
    }
}
