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
import com.extole.reporting.rest.report.sql.SqlReportTypeDatabase;

public class SqlReportTypeCreateRequest extends ReportTypeCreateRequest {
    private static final String JSON_DATABASE = "database";
    private static final String JSON_QUERY = "query";

    private final SqlReportTypeDatabase database;
    private final String query;

    public SqlReportTypeCreateRequest(
        @JsonProperty(JSON_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_CATEGORIES) Optional<List<String>> categories,
        @JsonProperty(JSON_SCOPES) Optional<Set<ReportTypeScope>> scopes,
        @JsonProperty(JSON_VISIBILITY) Optional<ReportTypeVisibility> visibility,
        @JsonProperty(JSON_FORMATS) Optional<List<ReportFormat>> formats,
        @JsonProperty(JSON_ALLOWED_SCOPES) Optional<Set<ReportTypeScope>> allowedScopes,
        @JsonProperty(JSON_DATA_START) Optional<Instant> dataStart,
        @JsonProperty(JSON_DATABASE) SqlReportTypeDatabase database,
        @JsonProperty(JSON_QUERY) String query,
        @JsonProperty(JSON_TAGS) Optional<Set<ReportTypeTagRequest>> tags) {
        super(ReportType.SQL, displayName, description, categories, scopes, visibility,
            formats, allowedScopes, dataStart, tags);
        this.database = database;
        this.query = query;
    }

    @JsonProperty(JSON_DATABASE)
    public SqlReportTypeDatabase getDatabase() {
        return database;
    }

    @JsonProperty(JSON_QUERY)
    public String getQuery() {
        return query;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportTypeCreateRequest.Builder<Builder> {
        private SqlReportTypeDatabase database;
        private String query;

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

        public SqlReportTypeCreateRequest build() {
            return new SqlReportTypeCreateRequest(displayName, description, categories, scopes, visibility, formats,
                allowedScopes, dataStart, database, query, tags);
        }
    }
}
