package com.extole.reporting.rest.report.execution;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.ReportTypeScope;

public class CreateReportRequest {
    @Deprecated // TODO should delete after switch ENG-20815
    private static final String NAME = "name";
    private static final String REPORT_TYPE = "report_type";
    private static final String DISPLAY_NAME = "display_name";
    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    private static final String FORMAT = "format";
    private static final String FORMATS = "formats";
    private static final String PARAMETERS = "parameters";
    private static final String RERUN = "rerun";
    private static final String TAGS = "tags";
    private static final String SFTP_REPORT_NAME = "sftp_report_name";
    private static final String SCOPES = "scopes";
    private static final String SFTP_SERVER_ID = "sftp_server_id";

    @Deprecated // TODO should delete after switch ENG-20815
    private final String name;
    private final String reportType;
    private final String displayName;
    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    private final ReportFormat format;
    private final List<ReportFormat> formats;
    private final Map<String, String> parameters;
    private final Boolean rerun;
    private final Set<String> tags;
    private final String sftpReportName;
    private final Set<ReportTypeScope> scopes;
    private final String sftpServerId;

    public CreateReportRequest(
        @Deprecated // TODO should delete after switch ENG-20815
        @JsonProperty(NAME) String name,
        @JsonProperty(REPORT_TYPE) String reportType,
        @Nullable @JsonProperty(DISPLAY_NAME) String displayName,
        @Deprecated // TODO delete ENG-7003
        @Nullable @JsonProperty(FORMAT) ReportFormat format,
        @Nullable @JsonProperty(FORMATS) List<ReportFormat> formats,
        @JsonProperty(PARAMETERS) Map<String, String> parameters,
        @Nullable @JsonProperty(RERUN) Boolean rerun,
        @Nullable @JsonProperty(TAGS) Set<String> tags,
        @Nullable @JsonProperty(SFTP_REPORT_NAME) String sftpReportName,
        @Nullable @JsonProperty(SCOPES) Set<ReportTypeScope> scopes,
        @Nullable @JsonProperty(SFTP_SERVER_ID) String sftpServerId) {
        this.name = reportType != null ? reportType : name;
        this.reportType = reportType != null ? reportType : name;
        this.displayName = displayName;
        this.format = format;
        this.formats = formats != null ? formats : Collections.emptyList();
        this.parameters = parameters != null ? parameters : Collections.emptyMap();
        this.rerun = rerun != null ? rerun : Boolean.TRUE;
        this.tags = tags;
        this.sftpReportName = sftpReportName;
        this.scopes = scopes != null ? Collections.unmodifiableSet(scopes) : null;
        this.sftpServerId = sftpServerId;
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

    @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
    @Nullable
    @JsonProperty(FORMAT)
    public ReportFormat getFormat() {
        return format;
    }

    @Nullable
    @JsonProperty(FORMATS)
    public List<ReportFormat> getFormats() {
        return formats;
    }

    @JsonProperty(PARAMETERS)
    public Map<String, String> getParameters() {
        return parameters;
    }

    @JsonProperty(RERUN)
    public Boolean rerun() {
        return rerun;
    }

    @Nullable
    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @Nullable
    @JsonProperty(SFTP_REPORT_NAME)
    public String getSftpReportName() {
        return sftpReportName;
    }

    @Nullable
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
        private String reportType;
        private String displayName;
        // TODO ENG-7003 should delete as soon as reporting version increments
        private ReportFormat format;
        private List<ReportFormat> formats;
        private Map<String, String> parameters;
        private Boolean rerun;
        private Set<String> tags;
        private String sftpReportName;
        private Set<ReportTypeScope> scopes;
        private String sftpServerId;

        private Builder() {
        }

        public Builder withReportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        @Deprecated // TODO should delete as soon as reporting version increments ENG-7003
        public Builder withFormat(ReportFormat format) {
            this.format = format;
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

        public Builder withRerun(Boolean rerun) {
            this.rerun = rerun;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withSftpReportName(String sftpReportName) {
            this.sftpReportName = sftpReportName;
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

        public CreateReportRequest build() {
            return new CreateReportRequest(reportType, reportType, displayName, format, formats, parameters, rerun,
                tags, sftpReportName, scopes, sftpServerId);
        }
    }
}
