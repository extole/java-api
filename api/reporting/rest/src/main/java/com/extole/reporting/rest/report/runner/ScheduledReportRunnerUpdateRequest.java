package com.extole.reporting.rest.report.runner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.api.report_runner.ReportRunnerExecutionContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.schedule.ScheduleFrequency;

public class ScheduledReportRunnerUpdateRequest extends ReportRunnerUpdateRequest {

    private static final String JSON_FREQUENCY = "frequency";
    private static final String JSON_SCHEDULE_START_DATE = "schedule_start_date";
    private static final String JSON_LEGACY_SFTP_REPORT_NAME_FORMAT = "legacy_sftp_report_name_format";

    private final Omissible<ScheduleFrequency> frequency;
    private final Omissible<ZonedDateTime> scheduleStartDate;
    private final Omissible<Boolean> legacySftpReportNameFormat;

    public ScheduledReportRunnerUpdateRequest(
        @Parameter(description = "Display name of the report runner. Has to be unique for client. " +
            "Must be present for create operation.")
        @JsonProperty(JSON_NAME) Omissible<String> name,
        @Parameter(description = "Report type name of the report runner. Must be present for create operation.")
        @JsonProperty(JSON_REPORT_TYPE) Omissible<String> reportType,
        @Parameter(description = "Optional list of formats which will get generated by the report runner, " +
            "defaults to JSON.")
        @JsonProperty(JSON_FORMATS) Omissible<List<ReportFormat>> formats,
        @Parameter(description = "List of report parameters, supported options depend on the report type.")
        @JsonProperty(JSON_PARAMETERS) Omissible<Map<String, String>> parameters,
        @Parameter(description = "Optional list of tags.")
        @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags,
        @Parameter(description = "Optional list of scopes.")
        @JsonProperty(JSON_SCOPES) Omissible<Set<ReportTypeScope>> scopes,
        @Parameter(description = "Optional sftp server to which Extole has to deliver generated reports.")
        @JsonProperty(JSON_SFTP_SERVER_ID) Omissible<Optional<String>> sftpServerId,
        @JsonProperty(JSON_PAUSE_INFO) Omissible<Optional<PauseInfoRequest>> pauseInfo,
        @JsonProperty(JSON_MERGING_CONFIGURATION) Omissible<Optional<MergingConfigurationRequest>> mergingConfiguration,
        @JsonProperty(JSON_DELIVER_EMPTY_REPORTS_TO_SFTP) Omissible<Optional<Boolean>> deliverEmptyReportsToSftp,
        @Parameter(description = "Frequency of the report runner execution. Must be present for create operation.")
        @JsonProperty(JSON_FREQUENCY) Omissible<ScheduleFrequency> frequency,
        @Parameter(description = "First date of the report runner execution. Must be present for create operation.")
        @JsonProperty(JSON_SCHEDULE_START_DATE) Omissible<ZonedDateTime> scheduleStartDate,
        @Parameter(description = "Optional flag to generate SFTP report name in the legacy format.")
        @JsonProperty(JSON_LEGACY_SFTP_REPORT_NAME_FORMAT) Omissible<Boolean> legacySftpReportNameFormat,
        @JsonProperty(JSON_REPORT_NAME_PATTERN)
        Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> reportNamePattern,
        @JsonProperty(JSON_SFTP_REPORT_NAME_PATTERN)
        Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> sftpReportNamePattern) {
        super(ReportRunnerType.SCHEDULED, name, reportType, formats, parameters, tags, scopes, sftpServerId, pauseInfo,
            mergingConfiguration, deliverEmptyReportsToSftp, reportNamePattern, sftpReportNamePattern);
        this.frequency = frequency;
        this.scheduleStartDate = scheduleStartDate;
        this.legacySftpReportNameFormat = legacySftpReportNameFormat;
    }

    @JsonProperty(JSON_FREQUENCY)
    public Omissible<ScheduleFrequency> getFrequency() {
        return frequency;
    }

    @JsonProperty(JSON_SCHEDULE_START_DATE)
    public Omissible<ZonedDateTime> getScheduleStartDate() {
        return scheduleStartDate;
    }

    @JsonProperty(JSON_LEGACY_SFTP_REPORT_NAME_FORMAT)
    public Omissible<Boolean> isLegacySftpReportNameFormat() {
        return legacySftpReportNameFormat;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ReportRunnerUpdateRequest.Builder<Builder> {
        private Omissible<ScheduleFrequency> frequency = Omissible.omitted();
        private Omissible<ZonedDateTime> scheduleStartDate = Omissible.omitted();
        private Omissible<Boolean> legacySftpReportNameFormat = Omissible.omitted();

        private Builder() {
        }

        public Builder withFrequency(ScheduleFrequency frequency) {
            this.frequency = Omissible.of(frequency);
            return this;
        }

        public Builder withScheduleStartDate(ZonedDateTime scheduleStartDate) {
            this.scheduleStartDate = Omissible.of(scheduleStartDate);
            return this;
        }

        public Builder withLegacySftpReportNameFormat(boolean legacySftpReportNameFormat) {
            this.legacySftpReportNameFormat = Omissible.of(Boolean.valueOf(legacySftpReportNameFormat));
            return this;
        }

        public ScheduledReportRunnerUpdateRequest build() {
            return new ScheduledReportRunnerUpdateRequest(name, reportType, formats, parameters, tags, scopes,
                sftpServerId, pauseInfo, mergingConfiguration, deliverEmptyReportsToSftp, frequency, scheduleStartDate,
                legacySftpReportNameFormat, reportNamePattern, sftpReportNamePattern);
        }
    }
}
