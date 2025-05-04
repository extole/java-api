package com.extole.reporting.rest.report.schedule;

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

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class ReportScheduleResponse {
    private static final String REPORT_SCHEDULE_ID = "report_schedule_id";
    private static final String NAME = "name";
    private static final String REPORT_TYPE = "report_type";
    private static final String FREQUENCY = "frequency";
    private static final String SCHEDULE_START_DATE = "schedule_start_date";
    private static final String FORMATS = "formats";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String VERSION = "version";
    private static final String PARAMETERS = "parameters";
    private static final String LEGACY_SFTP_REPORT_NAME_FORMAT = "legacy_sftp_report_name_format";
    private static final String SCOPES = "scopes";
    private static final String TAGS = "tags";
    private static final String USER_ID = "user_id";
    private static final String SFTP_SERVER_ID = "sftp_server_id";

    private final String reportScheduleId;
    private final String name;
    private final String reportType;
    private final ScheduleFrequency frequency;
    private final ZonedDateTime scheduleStartDate;
    private final List<ReportFormat> formats;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final int version;
    private final Map<String, ReportParameterResponse> parameters;
    private final boolean legacySftpReportNameFormat;
    private final Set<ReportTypeScope> scopes;
    private final Set<String> tags;
    private final String userId;
    private final String sftpServerId;

    public ReportScheduleResponse(
        @JsonProperty(REPORT_SCHEDULE_ID) String reportScheduleId,
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @JsonProperty(FREQUENCY) ScheduleFrequency frequency,
        @JsonProperty(SCHEDULE_START_DATE) ZonedDateTime scheduleStartDate,
        @JsonProperty(FORMATS) List<ReportFormat> formats,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(VERSION) int version,
        @JsonProperty(PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(LEGACY_SFTP_REPORT_NAME_FORMAT) boolean legacySftpReportNameFormat,
        @JsonProperty(SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(USER_ID) String userId,
        @Nullable @JsonProperty(SFTP_SERVER_ID) String sftpServerId) {
        this.reportScheduleId = reportScheduleId;
        this.name = name;
        this.reportType = reportType;
        this.frequency = frequency;
        this.scheduleStartDate = scheduleStartDate;
        this.formats = formats;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.version = version;
        this.parameters = parameters;
        this.legacySftpReportNameFormat = legacySftpReportNameFormat;
        this.scopes = scopes != null ? Collections.unmodifiableSet(scopes) : Collections.emptySet();
        this.tags = tags != null ? Collections.unmodifiableSet(tags) : Collections.emptySet();
        this.userId = userId;
        this.sftpServerId = sftpServerId;
    }

    @JsonProperty(REPORT_SCHEDULE_ID)
    public String getReportScheduleId() {
        return reportScheduleId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(REPORT_TYPE)
    public String getReportType() {
        return reportType;
    }

    @JsonProperty(FREQUENCY)
    public ScheduleFrequency getFrequency() {
        return frequency;
    }

    @JsonProperty(SCHEDULE_START_DATE)
    public ZonedDateTime getScheduleStartDate() {
        return scheduleStartDate;
    }

    @JsonProperty(FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public Optional<ZonedDateTime> getUpdatedDate() {
        return Optional.ofNullable(updatedDate);
    }

    @JsonProperty(VERSION)
    public int getVersion() {
        return version;
    }

    @JsonProperty(PARAMETERS)
    public Map<String, ReportParameterResponse> getParameters() {
        return parameters;
    }

    @JsonProperty(LEGACY_SFTP_REPORT_NAME_FORMAT)
    public boolean isLegacySftpReportNameFormat() {
        return legacySftpReportNameFormat;
    }

    @JsonProperty(SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(USER_ID)
    public String getUserId() {
        return userId;
    }

    @Nullable
    @JsonProperty(SFTP_SERVER_ID)
    public String getSftpServerId() {
        return sftpServerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String reportScheduleId;
        private String name;
        private String reportType;
        private ScheduleFrequency frequency;
        private ZonedDateTime scheduleStartDate;
        private List<ReportFormat> formats = Lists.newArrayList();
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;
        private int version;
        private Map<String, ReportParameterResponse> parameters = Maps.newHashMap();
        private boolean legacySftpReportNameFormat;
        private Set<ReportTypeScope> scopes;
        private Set<String> tags;
        private String userId;
        private String sftpServerId;

        private Builder() {
        }

        public Builder withReportScheduleId(String reportScheduleId) {
            this.reportScheduleId = reportScheduleId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withFrequency(ScheduleFrequency scheduleFrequency) {
            this.frequency = scheduleFrequency;
            return this;
        }

        public Builder withScheduleStartDate(ZonedDateTime scheduleStartDate) {
            this.scheduleStartDate = scheduleStartDate;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = Lists.newArrayList(formats);
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder withVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder withParameters(Map<String, ReportParameterResponse> parameters) {
            this.parameters = Maps.newHashMap(parameters);
            return this;
        }

        public Builder withLegacySftpReportNameFormat(boolean legacySftpReportNameFormat) {
            this.legacySftpReportNameFormat = legacySftpReportNameFormat;
            return this;
        }

        public Builder withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withSftpServerId(String sftpServerId) {
            this.sftpServerId = sftpServerId;
            return this;
        }

        public ReportScheduleResponse build() {
            return new ReportScheduleResponse(reportScheduleId, name, reportType, frequency,
                scheduleStartDate, formats, createdDate, updatedDate, version, parameters,
                legacySftpReportNameFormat, scopes, tags, userId, sftpServerId);
        }
    }
}
