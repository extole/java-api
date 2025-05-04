package com.extole.reporting.rest.report.schedule;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class UpdateReportScheduleRequest {
    private static final String NAME = "name";
    private static final String REPORT_TYPE = "report_type";
    private static final String FREQUENCY = "frequency";
    private static final String SCHEDULE_START_DATE = "schedule_start_date";
    private static final String FORMATS = "formats";
    private static final String PARAMETERS = "parameters";
    private static final String LEGACY_SFTP_REPORT_NAME_FORMAT = "legacy_sftp_report_name_format";
    private static final String SCOPES = "scopes";
    private static final String TAGS = "tags";
    private static final String SFTP_SERVER_ID = "sftp_server_id";

    private final String name;
    private final String reportType;
    private final ScheduleFrequency frequency;
    private final ZonedDateTime scheduleStartDate;
    private final List<ReportFormat> formats;
    private final Map<String, String> parameters;
    private final Boolean legacySftpReportNameFormat;
    private final Set<ReportTypeScope> scopes;
    private final Set<String> tags;
    private final String sftpServerId;

    public UpdateReportScheduleRequest(
        @Nullable @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(REPORT_TYPE) String reportType,
        @Nullable @JsonProperty(FREQUENCY) ScheduleFrequency frequency,
        @Nullable @JsonProperty(SCHEDULE_START_DATE) ZonedDateTime scheduleStartDate,
        @Nullable @JsonProperty(FORMATS) List<ReportFormat> formats,
        @Nullable @JsonProperty(PARAMETERS) Map<String, String> parameters,
        @Nullable @JsonProperty(LEGACY_SFTP_REPORT_NAME_FORMAT) Boolean legacySftpReportNameFormat,
        @Nullable @JsonProperty(SCOPES) Set<ReportTypeScope> scopes,
        @Nullable @JsonProperty(TAGS) Set<String> tags,
        @Nullable @JsonProperty(SFTP_SERVER_ID) String sftpServerId) {
        this.name = name;
        this.reportType = reportType;
        this.frequency = frequency;
        this.scheduleStartDate = scheduleStartDate;
        this.formats = formats;
        this.parameters = parameters;
        this.legacySftpReportNameFormat = legacySftpReportNameFormat;
        this.scopes = scopes != null ? Collections.unmodifiableSet(scopes) : null;
        this.tags = tags != null ? Collections.unmodifiableSet(tags) : null;
        this.sftpServerId = sftpServerId;
    }

    @Nullable
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(REPORT_TYPE)
    public String getReportType() {
        return reportType;
    }

    @Nullable
    @JsonProperty(FREQUENCY)
    public ScheduleFrequency getFrequency() {
        return frequency;
    }

    @JsonProperty(SCHEDULE_START_DATE)
    public Optional<ZonedDateTime> getScheduleStartDate() {
        return Optional.ofNullable(scheduleStartDate);
    }

    @Nullable
    @JsonProperty(FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @Nullable
    @JsonProperty(PARAMETERS)
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Nullable
    @JsonProperty(LEGACY_SFTP_REPORT_NAME_FORMAT)
    public Boolean isLegacySftpReportNameFormat() {
        return legacySftpReportNameFormat;
    }

    @Nullable
    @JsonProperty(SCOPES)
    public Set<ReportTypeScope> getScopes() {
        return scopes;
    }

    @Nullable
    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
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
        private String name;
        private String reportType;
        private ScheduleFrequency frequency;
        private ZonedDateTime scheduleStartDate;
        private List<ReportFormat> formats;
        private Map<String, String> parameters;
        private Boolean legacySftpReportNameFormat;
        private Set<ReportTypeScope> scopes;
        private Set<String> tags;
        private String sftpServerId;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withFrequency(ScheduleFrequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder withScheduleStartDate(ZonedDateTime scheduleStartDate) {
            this.scheduleStartDate = scheduleStartDate;
            return this;
        }

        public Builder withFormats(List<ReportFormat> formats) {
            this.formats = formats;
            return this;
        }

        public Builder withParameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder withLegacySftpReportNameFormat(Boolean legacySftpReportNameFormat) {
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

        public Builder withSftpServerId(String sftpServerId) {
            this.sftpServerId = sftpServerId;
            return this;
        }

        public UpdateReportScheduleRequest build() {
            return new UpdateReportScheduleRequest(name, reportType, frequency, scheduleStartDate, formats,
                parameters, legacySftpReportNameFormat, scopes, tags, sftpServerId);
        }
    }
}
