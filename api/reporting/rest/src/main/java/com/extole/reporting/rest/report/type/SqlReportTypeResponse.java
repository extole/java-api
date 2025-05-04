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
import com.extole.reporting.rest.report.sql.SqlReportTypeDatabase;

public class SqlReportTypeResponse extends ReportTypeResponse {
    private static final String JSON_DATABASE = "database";
    private static final String JSON_QUERY = "query";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";

    private final SqlReportTypeDatabase database;
    private final String query;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public SqlReportTypeResponse(
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
        @JsonProperty(JSON_DATABASE) SqlReportTypeDatabase database,
        @JsonProperty(JSON_QUERY) String query,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagResponse>> tags) {
        super(ReportType.SQL, name, displayName, description, executorType, categories, scopes, visibility, parameters,
            formats, allowedScopes, previewColumns, dataStart, tags);
        this.database = database;
        this.query = query;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_DATABASE)
    public SqlReportTypeDatabase getDatabase() {
        return database;
    }

    @JsonProperty(JSON_QUERY)
    public String getQuery() {
        return query;
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
        private SqlReportTypeDatabase database;
        private String query;
        private ZonedDateTime createdDate;
        private ZonedDateTime updateDate;

        private Builder() {
        }

        public Builder withDatabase(SqlReportTypeDatabase database) {
            this.database = database;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdateDate(ZonedDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public SqlReportTypeResponse build() {
            return new SqlReportTypeResponse(name, displayName, description, executorType, categories, scopes,
                visibility, parameters, formats, allowedScopes, previewColumns, dataStart, database, query, createdDate,
                updateDate, tags);
        }
    }
}
