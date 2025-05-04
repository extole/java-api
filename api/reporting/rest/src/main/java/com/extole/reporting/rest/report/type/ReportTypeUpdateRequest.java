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
    property = ReportTypeUpdateRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SqlReportTypeUpdateRequest.class, name = "SQL"),
    @JsonSubTypes.Type(value = ConfiguredReportTypeUpdateRequest.class, name = "CONFIGURED"),
    @JsonSubTypes.Type(value = SparkReportTypeUpdateRequest.class, name = "SPARK"),
    @JsonSubTypes.Type(value = DashboardReportTypeUpdateRequest.class, name = "DASHBOARD")
})
public abstract class ReportTypeUpdateRequest {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_TAGS = "tags";

    private final ReportType type;
    private final Optional<Set<ReportTypeTagRequest>> tags;

    public ReportTypeUpdateRequest(
        ReportType type,
        Optional<Set<ReportTypeTagRequest>> tags) {
        this.type = type;
        this.tags = tags;
    }

    @JsonProperty(JSON_TYPE)
    public ReportType getType() {
        return type;
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
