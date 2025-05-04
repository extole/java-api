package com.extole.reporting.rest.report.runner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.schedule.ScheduleFrequency;

public class ScheduledReportRunnerViewResponse extends ReportRunnerViewResponse {
    private static final String JSON_FREQUENCY = "frequency";
    private static final String JSON_SCHEDULE_START_DATE = "schedule_start_date";
    private static final String JSON_LEGACY_SFTP_REPORT_NAME_FORMAT = "legacy_sftp_report_name_format";

    private final ScheduleFrequency frequency;
    private final ZonedDateTime scheduleStartDate;
    private final boolean legacySftpReportNameFormat;

    @JsonCreator
    public ScheduledReportRunnerViewResponse(
        @JsonProperty(ReportRunnerResponse.JSON_ID) String id,
        @JsonProperty(ReportRunnerResponse.JSON_NAME) String name,
        @JsonProperty(ReportRunnerResponse.JSON_REPORT_TYPE_NAME) String reportTypeName,
        @JsonProperty(ReportRunnerResponse.JSON_FORMATS) List<ReportFormat> formats,
        @JsonProperty(ReportRunnerResponse.JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(ReportRunnerResponse.JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(ReportRunnerResponse.JSON_PARAMETERS) Map<String, ReportParameterResponse> parameters,
        @JsonProperty(ReportRunnerResponse.JSON_SCOPES) Set<ReportTypeScope> scopes,
        @JsonProperty(ReportRunnerResponse.JSON_TAGS) Set<String> tags,
        @JsonProperty(ReportRunnerResponse.JSON_USER_ID) String userId,
        @Nullable @JsonProperty(ReportRunnerResponse.SFTP_SERVER_ID) String sftpServerId,
        @JsonProperty(ReportRunnerResponse.JSON_PAUSED) Optional<PauseInfoResponse> paused,
        @JsonProperty(ReportRunnerResponse.JSON_MERGING_CONFIGURATION) Optional<
            MergingConfigurationResponse> mergingConfiguration,
        @JsonProperty(JSON_FREQUENCY) ScheduleFrequency frequency,
        @JsonProperty(JSON_SCHEDULE_START_DATE) ZonedDateTime scheduleStartDate,
        @JsonProperty(JSON_LEGACY_SFTP_REPORT_NAME_FORMAT) boolean legacySftpReportNameFormat,
        @JsonProperty(JSON_NEXT_EXECUTION_DATE) ZonedDateTime nextExecutionDate) {
        super(ReportRunnerType.SCHEDULED.name(), id, name, reportTypeName, formats, createdDate, updatedDate,
            parameters, scopes, tags, userId, sftpServerId, paused, mergingConfiguration, nextExecutionDate);
        this.frequency = frequency;
        this.scheduleStartDate = scheduleStartDate;
        this.legacySftpReportNameFormat = legacySftpReportNameFormat;
    }

    @JsonProperty(JSON_FREQUENCY)
    public ScheduleFrequency getFrequency() {
        return frequency;
    }

    @JsonProperty(JSON_SCHEDULE_START_DATE)
    public ZonedDateTime getScheduleStartDate() {
        return scheduleStartDate;
    }

    @JsonProperty(JSON_LEGACY_SFTP_REPORT_NAME_FORMAT)
    public boolean isLegacySftpReportNameFormat() {
        return legacySftpReportNameFormat;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportRunnerViewResponse.Builder {
        private ScheduleFrequency frequency;
        private ZonedDateTime scheduleStartDate;
        private boolean legacySftpReportNameFormat;

        private Builder() {
        }

        public Builder withFrequency(ScheduleFrequency scheduleFrequency) {
            this.frequency = scheduleFrequency;
            return this;
        }

        public Builder withScheduleStartDate(ZonedDateTime scheduleStartDate) {
            this.scheduleStartDate = scheduleStartDate;
            return this;
        }

        public Builder withLegacySftpReportNameFormat(boolean legacySftpReportNameFormat) {
            this.legacySftpReportNameFormat = legacySftpReportNameFormat;
            return this;
        }

        public ScheduledReportRunnerViewResponse build() {
            return new ScheduledReportRunnerViewResponse(id, name, reportTypeName, formats, createdDate, updatedDate,
                parameters, scopes, tags, userId, sftpServerId, paused, mergingConfiguration, frequency,
                scheduleStartDate, legacySftpReportNameFormat, nextExecutionDate);
        }
    }
}
