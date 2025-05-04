package com.extole.reporting.rest.report.runner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.api.report_runner.ReportRunnerExecutionContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportRunnerCreateRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScheduledReportRunnerCreateRequest.class, name = "SCHEDULED"),
    @JsonSubTypes.Type(value = RefreshingReportRunnerCreateRequest.class, name = "REFRESHING")
})
public abstract class ReportRunnerCreateRequest {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_NAME = "name";
    protected static final String JSON_REPORT_TYPE = "report_type";
    protected static final String JSON_FORMATS = "formats";
    protected static final String JSON_PARAMETERS = "parameters";
    protected static final String JSON_SCOPES = "scopes";
    protected static final String JSON_TAGS = "tags";
    protected static final String JSON_SFTP_SERVER_ID = "sftp_server_id";
    protected static final String JSON_PAUSE_INFO = "pause_info";
    protected static final String JSON_MERGING_CONFIGURATION = "merging_configuration";
    protected static final String JSON_DELIVER_EMPTY_REPORTS_TO_SFTP = "deliver_empty_reports_to_sftp";
    protected static final String JSON_REPORT_NAME_PATTERN = "report_name_pattern";
    protected static final String JSON_SFTP_REPORT_NAME_PATTERN = "sftp_report_name_pattern";

    private final ReportRunnerType type;
    private final String name;
    private final String reportType;
    private final Omissible<List<ReportFormat>> formats;
    private final Omissible<Map<String, String>> parameters;
    private final Omissible<Set<String>> tags;
    private final Omissible<Set<ReportTypeScope>> scopes;
    private final Omissible<Optional<String>> sftpServerId;
    private final Omissible<Optional<PauseInfoRequest>> pauseInfo;
    private final Omissible<Optional<MergingConfigurationRequest>> mergingConfiguration;
    private final Omissible<Optional<Boolean>> deliverEmptyReportsToSftp;
    private final Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> reportNamePattern;
    private final Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> sftpReportNamePattern;

    public ReportRunnerCreateRequest(
        ReportRunnerType type,
        String name,
        String reportType,
        Omissible<List<ReportFormat>> formats,
        Omissible<Map<String, String>> parameters,
        Omissible<Set<String>> tags,
        Omissible<Set<ReportTypeScope>> scopes,
        Omissible<Optional<String>> sftpServerId,
        Omissible<Optional<PauseInfoRequest>> pauseInfo,
        Omissible<Optional<MergingConfigurationRequest>> mergingConfiguration,
        Omissible<Optional<Boolean>> deliverEmptyReportsToSftp,
        Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> reportNamePattern,
        Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> sftpReportNamePattern) {
        this.type = type;
        this.name = name;
        this.reportType = reportType;
        this.formats = formats;
        this.parameters = parameters;
        this.tags = tags;
        this.scopes = scopes;
        this.sftpServerId = sftpServerId;
        this.pauseInfo = pauseInfo;
        this.mergingConfiguration = mergingConfiguration;
        this.deliverEmptyReportsToSftp = deliverEmptyReportsToSftp;
        this.reportNamePattern = reportNamePattern;
        this.sftpReportNamePattern = sftpReportNamePattern;
    }

    @JsonProperty(JSON_TYPE)
    public ReportRunnerType getType() {
        return type;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_REPORT_TYPE)
    public String getReportType() {
        return reportType;
    }

    @JsonProperty(JSON_FORMATS)
    public Omissible<List<ReportFormat>> getFormats() {
        return formats;
    }

    @JsonProperty(JSON_PARAMETERS)
    public Omissible<Map<String, String>> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_SCOPES)
    public Omissible<Set<ReportTypeScope>> getScopes() {
        return scopes;
    }

    @JsonProperty(JSON_SFTP_SERVER_ID)
    public Omissible<Optional<String>> getSftpServerId() {
        return sftpServerId;
    }

    @JsonProperty(JSON_PAUSE_INFO)
    public Omissible<Optional<PauseInfoRequest>> getPauseInfo() {
        return pauseInfo;
    }

    @JsonProperty(JSON_MERGING_CONFIGURATION)
    public Omissible<Optional<MergingConfigurationRequest>> getMergingConfiguration() {
        return mergingConfiguration;
    }

    @JsonProperty(JSON_DELIVER_EMPTY_REPORTS_TO_SFTP)
    public Omissible<Optional<Boolean>> getDeliverEmptyReportsToSftp() {
        return deliverEmptyReportsToSftp;
    }

    @JsonProperty(JSON_REPORT_NAME_PATTERN)
    public Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> getReportNamePattern() {
        return reportNamePattern;
    }

    @JsonProperty(JSON_SFTP_REPORT_NAME_PATTERN)
    public Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> getSftpReportNamePattern() {
        return sftpReportNamePattern;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T> {
        protected String name;
        protected String reportType;
        protected Omissible<List<ReportFormat>> formats = Omissible.omitted();
        protected Omissible<Map<String, String>> parameters = Omissible.omitted();
        protected Omissible<Set<String>> tags = Omissible.omitted();
        protected Omissible<Set<ReportTypeScope>> scopes = Omissible.omitted();
        protected Omissible<Optional<String>> sftpServerId = Omissible.omitted();
        protected Omissible<Optional<PauseInfoRequest>> pauseInfo = Omissible.omitted();
        protected Omissible<Optional<MergingConfigurationRequest>> mergingConfiguration = Omissible.omitted();
        protected Omissible<Optional<Boolean>> deliverEmptyReportsToSftp = Omissible.omitted();
        protected Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> reportNamePattern =
            Omissible.omitted();
        protected Omissible<RuntimeEvaluatable<ReportRunnerExecutionContext, String>> sftpReportNamePattern =
            Omissible.omitted();

        protected Builder() {
        }

        public T withName(String name) {
            this.name = name;
            return (T) this;
        }

        public T withReportType(String reportType) {
            this.reportType = reportType;
            return (T) this;
        }

        public T withFormats(List<ReportFormat> formats) {
            this.formats = Omissible.of(formats);
            return (T) this;
        }

        public T withParameters(Map<String, String> parameters) {
            this.parameters = Omissible.of(parameters);
            return (T) this;
        }

        public T withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return (T) this;
        }

        public T withScopes(Set<ReportTypeScope> scopes) {
            this.scopes = Omissible.of(scopes);
            return (T) this;
        }

        public T withSftpServerId(Optional<String> sftpServerId) {
            this.sftpServerId = Omissible.of(sftpServerId);
            return (T) this;
        }

        public T withPauseInfo(Optional<PauseInfoRequest> pauseInfo) {
            this.pauseInfo = Omissible.of(pauseInfo);
            return (T) this;
        }

        public T withMergingConfiguration(Optional<MergingConfigurationRequest> mergingConfiguration) {
            this.mergingConfiguration = Omissible.of(mergingConfiguration);
            return (T) this;
        }

        public T withDeliverEmptyReportsToSftp(Optional<Boolean> deliverEmptyReportsToSftp) {
            this.deliverEmptyReportsToSftp = Omissible.of(deliverEmptyReportsToSftp);
            return (T) this;
        }

        public T
            withReportNamePattern(RuntimeEvaluatable<ReportRunnerExecutionContext, String> reportNamePattern) {
            this.reportNamePattern = Omissible.of(reportNamePattern);
            return (T) this;
        }

        public T withSftpReportNamePattern(
            RuntimeEvaluatable<ReportRunnerExecutionContext, String> sftpReportNamePattern) {
            this.sftpReportNamePattern = Omissible.of(sftpReportNamePattern);
            return (T) this;
        }
    }
}
