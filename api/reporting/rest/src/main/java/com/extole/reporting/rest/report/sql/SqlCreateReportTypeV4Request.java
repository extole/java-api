package com.extole.reporting.rest.report.sql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;

public class SqlCreateReportTypeV4Request {
    private static final String JSON_NAME = "name";
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_CATEGORIES = "categories";
    private static final String JSON_DATABASE = "database";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_QUERY = "query";
    private static final String JSON_VISIBILITY = "visibility";
    private static final String JSON_ALLOWED_SCOPES = "allowed_scopes";

    private final String name;
    private final String displayName;
    private final String description;
    private final SqlReportTypeDatabase database;
    private final Optional<Set<ReportTypeScope>> scopes;
    private final String query;
    private final ReportTypeVisibility visibility;
    private final List<String> categories;
    private final Set<ReportTypeScope> allowedScopes;

    public SqlCreateReportTypeV4Request(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_DATABASE) SqlReportTypeDatabase database,
        @JsonProperty(JSON_SCOPES) Optional<Set<ReportTypeScope>> scopes,
        @JsonProperty(JSON_QUERY) String query,
        @JsonProperty(JSON_CATEGORIES) List<String> categories,
        @Nullable @JsonProperty(JSON_VISIBILITY) ReportTypeVisibility visibility,
        @Nullable @JsonProperty(JSON_ALLOWED_SCOPES) Set<ReportTypeScope> allowedScopes) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.database = database;
        this.scopes = scopes;
        this.query = query;
        this.categories = categories != null ? categories : Collections.emptyList();
        this.visibility = visibility != null ? visibility : ReportTypeVisibility.PUBLIC;
        this.allowedScopes = allowedScopes != null ? Collections.unmodifiableSet(allowedScopes) : null;
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

    @JsonProperty(JSON_DATABASE)
    public SqlReportTypeDatabase getDatabase() {
        return database;
    }

    @JsonProperty(JSON_SCOPES)
    public Optional<Set<ReportTypeScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_QUERY)
    public String getQuery() {
        return query;
    }

    @JsonProperty(JSON_CATEGORIES)
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_VISIBILITY)
    public ReportTypeVisibility getVisibility() {
        return visibility;
    }

    @Nullable
    @JsonProperty(JSON_ALLOWED_SCOPES)
    public Set<ReportTypeScope> getAllowedScopes() {
        return allowedScopes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String displayName;
        private String description;
        private SqlReportTypeDatabase database;
        private Optional<Set<ReportTypeScope>> scopes;
        private String query;
        private ReportTypeVisibility visibility;
        private List<String> categories;
        private Set<ReportTypeScope> allowedScopes;

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

        public Builder withDatabase(SqlReportTypeDatabase database) {
            this.database = database;
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = Optional.ofNullable(scopes);
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withVisibility(ReportTypeVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder withCategories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder withAllowedScopes(Set<ReportTypeScope> allowedScopes) {
            this.allowedScopes = allowedScopes;
            return this;
        }

        public SqlCreateReportTypeV4Request build() {
            return new SqlCreateReportTypeV4Request(name, displayName, description, database, scopes, query, categories,
                visibility, allowedScopes);
        }
    }
}
