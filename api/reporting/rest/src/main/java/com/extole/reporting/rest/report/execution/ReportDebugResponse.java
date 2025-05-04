package com.extole.reporting.rest.report.execution;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportDebugResponse {
    private static final String REPORT_ID = "report_id";
    private static final String STATUS = "status";
    private static final String ERROR_CODE = "error_code";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String DEBUG_MESSAGE = "debug_message";

    private final String reportId;
    private final ReportStatus status;
    private final String errorCode;
    private final String errorMessage;
    private final String debugMessage;

    public ReportDebugResponse(
        @JsonProperty(REPORT_ID) String reportId,
        @JsonProperty(STATUS) ReportStatus status,
        @Nullable @JsonProperty(ERROR_CODE) String errorCode,
        @Nullable @JsonProperty(ERROR_MESSAGE) String errorMessage,
        @Nullable @JsonProperty(DEBUG_MESSAGE) String debugMessage) {
        this.reportId = reportId;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.debugMessage = debugMessage;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }

    @JsonProperty(STATUS)
    public ReportStatus getStatus() {
        return status;
    }

    @JsonProperty(ERROR_CODE)
    public Optional<String> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    @JsonProperty(ERROR_MESSAGE)
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    @JsonProperty(DEBUG_MESSAGE)
    public Optional<String> getDebugMessage() {
        return Optional.ofNullable(debugMessage);
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
        private ReportStatus status;
        private String errorCode;
        private String errorMessage;
        private String debugMessage;

        private Builder() {

        }

        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder withReportStatus(ReportStatus status) {
            this.status = status;
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder withDebugMessage(String debugMessage) {
            this.debugMessage = debugMessage;
            return this;
        }

        public ReportDebugResponse build() {
            return new ReportDebugResponse(reportId, status, errorCode, errorMessage, debugMessage);
        }
    }
}
