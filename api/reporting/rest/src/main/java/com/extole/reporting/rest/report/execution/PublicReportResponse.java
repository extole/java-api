package com.extole.reporting.rest.report.execution;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportTypeScope;

public class PublicReportResponse {
    private static final String ID = "id";
    private static final String REPORT_TYPE = "report_type";
    private static final String DISPLAY_NAME = "display_name";
    private static final String EXECUTOR_TYPE = "executor_type";
    private static final String FORMATS = "formats";
    private static final String USER_ID = "user_id";
    private static final String RESULT = "result";
    private static final String PARAMETERS = "parameters";
    private static final String TAGS = "tags";
    private static final String CREATED_DATE = "created_date";
    private static final String DOWNLOAD_URI = "download_uri";
    private static final String SCOPES = "scopes";

    private final String id;
    private final String reportType;
    private final String displayName;

    private final ReportExecutorType executorType;
    private final List<ReportFormat> formats;
    private final String userId;
    private final PublicReportResultResponse result;
    private final Map<String, ReportParameterResponse> parameters;
    private final Set<String> tags;
    private final ZonedDateTime createdDate;
    private final Optional<String> downloadUri;
    private final Set<ReportTypeScope> scopes;

    public PublicReportResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(DISPLAY_NAME) String displayName,
        @JsonProperty(EXECUTOR_TYPE) ReportExecutorType executorType,
        @JsonProperty(FORMATS) List<ReportFormat> formats,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(RESULT) PublicReportResultResponse result,
        @JsonProperty(PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(DOWNLOAD_URI) Optional<String> downloadUri,
        @JsonProperty(SCOPES) Set<ReportTypeScope> scopes) {
        this.id = id;
        this.reportType = reportType;
        this.displayName = displayName;
        this.executorType = executorType;
        this.formats = ImmutableList.copyOf(formats);
        this.userId = userId;
        this.result = result;
        this.parameters = ImmutableMap.copyOf(parameters);
        this.tags = ImmutableSet.copyOf(tags);
        this.createdDate = createdDate;
        this.downloadUri = downloadUri;
        this.scopes = ImmutableSet.copyOf(scopes);
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(REPORT_TYPE)
    public String getReportType() {
        return reportType;
    }

    @JsonProperty(DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(EXECUTOR_TYPE)
    public ReportExecutorType getExecutorType() {
        return executorType;
    }

    @JsonProperty(FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @JsonProperty(RESULT)
    public PublicReportResultResponse getResult() {
        return result;
    }

    @JsonProperty(PARAMETERS)
    public Map<String, ReportParameterResponse> getParameters() {
        return parameters;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(DOWNLOAD_URI)
    public Optional<String> getDownloadUri() {
        return downloadUri;
    }

    @JsonProperty(SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String reportId;
        private String reportType;
        private String displayName;

        private ReportExecutorType executorType;
        private List<ReportFormat> formats = Lists.newArrayList();
        private String userId;
        private PublicReportResultResponse result;
        private Map<String, ReportParameterResponse> parameters = Maps.newHashMap();
        private Set<String> tags = Sets.newHashSet();
        private ZonedDateTime createdDate;
        private Optional<String> downloadUri = Optional.empty();
        private Set<ReportTypeScope> scopes = Sets.newHashSet();

        private Builder() {
        }

        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withExecutorType(ReportExecutorType executorType) {
            this.executorType = executorType;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = Lists.newArrayList(formats);
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withResult(PublicReportResultResponse result) {
            this.result = result;
            return this;
        }

        public Builder withParameters(Map<String, ReportParameterResponse> parameters) {
            this.parameters = Maps.newHashMap(parameters);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Sets.newHashSet(tags);
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withDownloadUri(String downloadUri) {
            this.downloadUri = Optional.ofNullable(downloadUri);
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = Sets.newHashSet(scopes);
            return this;
        }

        public PublicReportResponse build() {
            return new PublicReportResponse(reportId, reportType, displayName, executorType, formats,
                userId, result, parameters, tags, createdDate, downloadUri, scopes);
        }
    }
}
