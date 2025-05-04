package com.extole.reporting.rest.report.execution;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportTypeScope;

public class ReportResponse {
    private static final String REPORT_ID = "report_id";
    @Deprecated // TODO should delete after switch ENG-20815
    private static final String NAME = "name";
    private static final String REPORT_TYPE = "report_type";
    private static final String DISPLAY_NAME = "display_name";
    private static final String EXECUTOR_TYPE = "executor_type";
    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    private static final String FORMAT = "format";
    private static final String FORMATS = "formats";
    private static final String STATUS = "status";
    private static final String USER_ID = "user_id";
    private static final String PARAMETERS = "parameters";
    private static final String TAGS = "tags";
    private static final String CREATED_DATE = "created_date";
    private static final String STARTED_DATE = "started_date";
    private static final String COMPLETED_DATE = "completed_date";
    private static final String ERROR_CODE = "error_code";
    private static final String DOWNLOAD_URI = "download_uri";
    private static final String SFTP_REPORT_NAME = "sftp_report_name";
    @Deprecated // TODO should be removed ENG-8856
    private static final String VISIBLE = "visible";
    private static final String SCOPES = "scopes";
    private static final String SFTP_SERVER_ID = "sftp_server_id";

    private final String reportId;
    @Deprecated // TODO should delete after switch ENG-20815
    private final String name;
    private final String reportType;
    private final String displayName;
    private final ReportExecutorType executorType;
    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    private final ReportFormat format;
    private final List<ReportFormat> formats;
    private final ReportStatus status;
    private final String userId;
    private final Map<String, ReportParameterResponse> parameters;
    private final Set<String> tags;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime startedDate;
    private final ZonedDateTime completedDate;
    private final String errorCode;
    private final String downloadUri;
    private final String sftpReportName;
    @Deprecated // TODO should be removed ENG-8856
    private final Boolean visible;
    private final Set<ReportTypeScope> scopes;
    private final String sftpServerId;

    public ReportResponse(
        @JsonProperty(REPORT_ID) String reportId,
        @Deprecated // TODO should delete after switch ENG-20815
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(DISPLAY_NAME) String displayName,
        @JsonProperty(EXECUTOR_TYPE) ReportExecutorType executorType,
        @Deprecated // TODO delete ENG-7003
        @JsonProperty(FORMAT) ReportFormat format,
        @JsonProperty(FORMATS) List<ReportFormat> formats,
        @JsonProperty(STATUS) ReportStatus status,
        @JsonProperty(USER_ID) String userId,
        @JsonProperty(PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @Nullable @JsonProperty(STARTED_DATE) ZonedDateTime startedDate,
        @Nullable @JsonProperty(COMPLETED_DATE) ZonedDateTime completedDate,
        @Nullable @JsonProperty(ERROR_CODE) String errorCode,
        @Nullable @JsonProperty(DOWNLOAD_URI) String downloadUri,
        @Nullable @JsonProperty(SFTP_REPORT_NAME) String sftpReportName,
        @JsonProperty(VISIBLE) Boolean visible,
        @Nullable @JsonProperty(SCOPES) Set<ReportTypeScope> scopes,
        @Nullable @JsonProperty(SFTP_SERVER_ID) String sftpServerId) {
        this.reportId = reportId;
        this.name = name;
        this.reportType = reportType;
        this.displayName = displayName;
        this.executorType = executorType;
        this.format = format;
        this.formats = formats;
        this.status = status;
        this.userId = userId;
        this.parameters = parameters;
        this.tags = tags;
        this.createdDate = createdDate;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
        this.errorCode = errorCode;
        this.downloadUri = downloadUri;
        this.sftpReportName = sftpReportName;
        this.visible = visible;
        this.scopes = scopes != null ? scopes : Collections.emptySet();
        this.sftpServerId = sftpServerId;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }

    @Deprecated // TODO should delete after switch ENG-20815
    @JsonProperty(NAME)
    public String getName() {
        return name;
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

    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    @JsonProperty(FORMAT)
    public ReportFormat getFormat() {
        return format;
    }

    @JsonProperty(STATUS)
    public ReportStatus getStatus() {
        return status;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
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

    @JsonProperty(STARTED_DATE)
    public Optional<ZonedDateTime> getStartedDate() {
        return Optional.ofNullable(startedDate);
    }

    @JsonProperty(COMPLETED_DATE)
    public Optional<ZonedDateTime> getCompletedDate() {
        return Optional.ofNullable(completedDate);
    }

    @Nullable
    @JsonProperty(ERROR_CODE)
    public String getErrorCode() {
        return errorCode;
    }

    @Nullable
    @JsonProperty(DOWNLOAD_URI)
    public String getDownloadUri() {
        return downloadUri;
    }

    @Nullable
    @JsonProperty(SFTP_REPORT_NAME)
    public String getSftpReportName() {
        return sftpReportName;
    }

    @Deprecated // TODO should be removed ENG-8856
    @JsonProperty(VISIBLE)
    public Boolean isVisible() {
        return visible;
    }

    @JsonProperty(SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @Nullable
    @JsonProperty(SFTP_SERVER_ID)
    public String getSftpServerId() {
        return sftpServerId;
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
        private ReportFormat format;
        private List<ReportFormat> formats = Lists.newArrayList();
        private ReportStatus status;
        private String userId;
        private Map<String, ReportParameterResponse> parameters = Maps.newHashMap();
        private Set<String> tags = Sets.newHashSet();
        private ZonedDateTime createdDate;
        private ZonedDateTime startedDate;
        private ZonedDateTime completedDate;
        private String errorCode;
        private String downloadUri;
        private String sftpReportName;
        @Deprecated // TODO should be removed ENG-8856
        private Boolean visible;
        private Set<ReportTypeScope> scopes;
        private String sftpServerId;

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

        public Builder withFormat(ReportFormat format) {
            this.format = format;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = Lists.newArrayList(formats);
            return this;
        }

        public Builder withStatus(ReportStatus status) {
            this.status = status;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
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

        public Builder withStartedDate(ZonedDateTime startedDate) {
            this.startedDate = startedDate;
            return this;
        }

        public Builder withCompletedDate(ZonedDateTime completedDate) {
            this.completedDate = completedDate;
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withDownloadUri(String downloadUri) {
            this.downloadUri = downloadUri;
            return this;
        }

        public Builder withSftpReportName(String sftpReportName) {
            this.sftpReportName = sftpReportName;
            return this;
        }

        @Deprecated // TODO should be removed ENG-8856
        public Builder withVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withSftpServerId(String sftpServerId) {
            this.sftpServerId = sftpServerId;
            return this;
        }

        public ReportResponse build() {
            return new ReportResponse(reportId, reportType, reportType, displayName, executorType, format, formats,
                status, userId, parameters, tags, createdDate, startedDate, completedDate, errorCode, downloadUri,
                sftpReportName, visible, scopes, sftpServerId);
        }
    }
}
