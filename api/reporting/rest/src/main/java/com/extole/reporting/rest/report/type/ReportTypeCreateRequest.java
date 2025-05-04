package com.extole.reporting.rest.report.type;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportType;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.execution.ReportFormat;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportTypeCreateRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SqlReportTypeCreateRequest.class, name = "SQL"),
    @JsonSubTypes.Type(value = ConfiguredReportTypeCreateRequest.class, name = "CONFIGURED")
})
public abstract class ReportTypeCreateRequest {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_DISPLAY_NAME = "display_name";
    protected static final String JSON_DESCRIPTION = "description";
    protected static final String JSON_CATEGORIES = "categories";
    protected static final String JSON_SCOPES = "scopes";
    protected static final String JSON_VISIBILITY = "visibility";
    protected static final String JSON_FORMATS = "formats";
    protected static final String JSON_ALLOWED_SCOPES = "allowed_scopes";
    protected static final String JSON_DATA_START = "data_start";
    protected static final String JSON_TAGS = "tags";

    private final ReportType type;
    private final Optional<String> displayName;
    private final Optional<String> description;
    private final Optional<List<String>> categories;
    private final Optional<Set<ReportTypeScope>> scopes;
    private final Optional<ReportTypeVisibility> visibility;
    private final Optional<List<ReportFormat>> formats;
    private final Optional<Set<ReportTypeScope>> allowedScopes;
    private final Optional<Instant> dataStart;
    private final Optional<Set<ReportTypeTagRequest>> tags;

    public ReportTypeCreateRequest(
        ReportType type,
        Optional<String> displayName,
        Optional<String> description,
        Optional<List<String>> categories,
        Optional<Set<ReportTypeScope>> scopes,
        Optional<ReportTypeVisibility> visibility,
        Optional<List<ReportFormat>> formats,
        Optional<Set<ReportTypeScope>> allowedScopes,
        Optional<Instant> dataStart,
        Optional<Set<ReportTypeTagRequest>> tags) {
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.categories = categories;
        this.scopes = scopes;
        this.visibility = visibility;
        this.formats = formats;
        this.allowedScopes = allowedScopes;
        this.dataStart = dataStart;
        this.tags = tags;
    }

    @JsonProperty(JSON_TYPE)
    public ReportType getType() {
        return type;
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

    @JsonProperty(JSON_TAGS)
    public Optional<Set<ReportTypeTagRequest>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T> {
        protected Optional<String> displayName = Optional.empty();
        protected Optional<String> description = Optional.empty();
        protected Optional<List<String>> categories = Optional.empty();
        protected Optional<Set<ReportTypeScope>> scopes = Optional.empty();
        protected Optional<ReportTypeVisibility> visibility = Optional.empty();
        protected Optional<List<ReportFormat>> formats = Optional.empty();
        protected Optional<Set<ReportTypeScope>> allowedScopes = Optional.empty();
        protected Optional<Instant> dataStart = Optional.empty();
        protected Optional<Set<ReportTypeTagRequest>> tags = Optional.empty();

        protected Builder() {
        }

        public T withDisplayName(String displayName) {
            this.displayName = Optional.ofNullable(displayName);
            return (T) this;
        }

        public T withDescription(String description) {
            this.description = Optional.ofNullable(description);
            return (T) this;
        }

        public T withCategories(List<String> categories) {
            this.categories = Optional.ofNullable(categories);
            return (T) this;
        }

        public T withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = Optional.ofNullable(scopes);
            return (T) this;
        }

        public T withVisibility(ReportTypeVisibility visibility) {
            this.visibility = Optional.ofNullable(visibility);
            return (T) this;
        }

        public T withFormats(List<ReportFormat> formats) {
            this.formats = Optional.ofNullable(formats);
            return (T) this;
        }

        public T withAllowedScopes(Set<ReportTypeScope> allowedScopes) {
            this.allowedScopes = Optional.ofNullable(allowedScopes);
            return (T) this;
        }

        public T withDataStart(Instant dataStart) {
            this.dataStart = Optional.ofNullable(dataStart);
            return (T) this;
        }

        public T withTags(Set<ReportTypeTagRequest> tags) {
            this.tags = Optional.ofNullable(tags);
            return (T) this;
        }
    }
}
