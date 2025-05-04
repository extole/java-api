package com.extole.reporting.rest.report.sql;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;

public class SqlReportTypeV4Response {
    private static final String JSON_NAME = "name";
    private static final String JSON_DISPLAY_NAME = "display_name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_CATEGORIES = "categories";
    private static final String JSON_TYPE = "executor_type";
    private static final String JSON_SCOPES = "scopes";
    private static final String JSON_PARAMETERS = "parameters";
    private static final String JSON_DATABASE = "database";
    private static final String JSON_QUERY = "query";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";
    private static final String JSON_VISIBILITY = "visibility";
    private static final String JSON_ALLOWED_SCOPES = "allowed_scopes";

    private final String name;
    private final String displayName;
    private final String description;
    private final List<String> categories;

    private final ReportExecutorType executorType;
    private final Set<ReportTypeScope> scopes;
    private final List<ReportTypeParameterDetailsResponse> parameters;
    private final SqlReportTypeDatabase database;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final String query;
    private final ReportTypeVisibility visibility;
    private final Set<ReportTypeScope> allowedScopes;

    public SqlReportTypeV4Response(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_DESCRIPTION) String description,
        @JsonProperty(JSON_CATEGORIES) List<String> categories,
        @JsonProperty(JSON_SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(JSON_DATABASE) SqlReportTypeDatabase database,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_QUERY) String query,
        @JsonProperty(JSON_PARAMETERS) List<ReportTypeParameterDetailsResponse> parameters,
        @JsonProperty(JSON_VISIBILITY) ReportTypeVisibility visibility,
        @JsonProperty(JSON_ALLOWED_SCOPES) Set<ReportTypeScope> allowedScopes) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.categories = categories != null ? categories : Collections.emptyList();
        this.executorType = ReportExecutorType.SQL;
        this.scopes = scopes != null ? scopes : Collections.emptySet();
        this.parameters = parameters != null ? parameters : Collections.emptyList();
        this.database = database;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.query = query;
        this.visibility = visibility;
        this.allowedScopes = allowedScopes != null ? allowedScopes : Collections.emptySet();
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

    @JsonProperty(JSON_TYPE)
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

    @JsonProperty(JSON_DATABASE)
    public SqlReportTypeDatabase getDatabase() {
        return database;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(JSON_QUERY)
    public String getQuery() {
        return query;
    }

    @JsonProperty(JSON_VISIBILITY)
    public ReportTypeVisibility getVisibility() {
        return visibility;
    }

    @JsonProperty(JSON_ALLOWED_SCOPES)
    public Set<ReportTypeScope> getAllowedScopes() {
        return allowedScopes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
