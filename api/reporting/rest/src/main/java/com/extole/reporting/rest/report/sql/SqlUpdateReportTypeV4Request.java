package com.extole.reporting.rest.report.sql;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;

public class SqlUpdateReportTypeV4Request {
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
    private final Set<ReportTypeScope> scopes;
    private final String query;
    private final List<String> categories;
    private final ReportTypeVisibility visibility;
    private final Set<ReportTypeScope> allowedScopes;

    public SqlUpdateReportTypeV4Request(
        @Nullable @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_DESCRIPTION) String description,
        @Nullable @JsonProperty(JSON_DATABASE) SqlReportTypeDatabase database,
        @Nullable @JsonProperty(JSON_SCOPES) Set<ReportTypeScope> scopes,
        @Nullable @JsonProperty(JSON_QUERY) String query,
        @JsonProperty(JSON_CATEGORIES) List<String> categories,
        @Nullable @JsonProperty(JSON_VISIBILITY) ReportTypeVisibility visibility,
        @Nullable @JsonProperty(JSON_ALLOWED_SCOPES) Set<ReportTypeScope> allowedScopes) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.database = database;
        this.scopes = scopes;
        this.query = query;
        this.categories = categories;
        this.visibility = visibility;
        this.allowedScopes = allowedScopes;
    }

    @Nullable
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

    @Nullable
    @JsonProperty(JSON_DATABASE)
    public SqlReportTypeDatabase getDatabase() {
        return database;
    }

    @Nullable
    @JsonProperty(JSON_SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @Nullable
    @JsonProperty(JSON_QUERY)
    public String getQuery() {
        return query;
    }

    @Nullable
    @JsonProperty(JSON_CATEGORIES)
    public List<String> getCategories() {
        return categories;
    }

    @Nullable
    @JsonProperty(JSON_VISIBILITY)
    public ReportTypeVisibility getVisibility() {
        return visibility;
    }

    @Nullable
    @JsonProperty(JSON_ALLOWED_SCOPES)
    public Set<ReportTypeScope> getAllowedScopes() {
        return allowedScopes;
    }
}
