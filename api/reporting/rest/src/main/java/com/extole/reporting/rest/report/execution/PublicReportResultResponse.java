package com.extole.reporting.rest.report.execution;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;

public class PublicReportResultResponse {
    private static final String STATUS = "status";
    private static final String STARTED_DATE = "started_date";
    private static final String COMPLETED_DATE = "completed_date";
    private static final String TOTAL_ROWS = "total_rows";
    private static final String FORMATS_INFO = "formats_info";

    private final ReportStatus status;
    private final Optional<ZonedDateTime> startedDate;
    private final Optional<ZonedDateTime> completedDate;
    private final Optional<Long> totalRows;

    private final Map<ReportFormat, FormatReportInfoResponse> formatsInfo;

    public PublicReportResultResponse(
        @JsonProperty(STATUS) ReportStatus status,
        @JsonProperty(STARTED_DATE) Optional<ZonedDateTime> startedDate,
        @JsonProperty(COMPLETED_DATE) Optional<ZonedDateTime> completedDate,
        @JsonProperty(TOTAL_ROWS) Optional<Long> totalRows,
        @JsonProperty(FORMATS_INFO) Map<ReportFormat, FormatReportInfoResponse> formatsInfo) {
        this.status = status;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
        this.totalRows = totalRows;
        this.formatsInfo = ImmutableMap.copyOf(formatsInfo);
    }

    @JsonProperty(STATUS)
    public ReportStatus getStatus() {
        return status;
    }

    @JsonProperty(STARTED_DATE)
    public Optional<ZonedDateTime> getStartedDate() {
        return startedDate;
    }

    @JsonProperty(COMPLETED_DATE)
    public Optional<ZonedDateTime> getCompletedDate() {
        return completedDate;
    }

    @JsonProperty(TOTAL_ROWS)
    public Optional<Long> getTotalRows() {
        return totalRows;
    }

    @JsonProperty(FORMATS_INFO)
    public Map<ReportFormat, FormatReportInfoResponse> getFormatsInfo() {
        return formatsInfo;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ReportStatus status;
        private Optional<ZonedDateTime> startedDate = Optional.empty();
        private Optional<ZonedDateTime> completedDate = Optional.empty();
        private Optional<Long> totalRows = Optional.empty();
        private Map<ReportFormat, FormatReportInfoResponse> formatsInfo = Maps.newHashMap();

        private Builder() {
        }

        public Builder withStatus(ReportStatus status) {
            this.status = status;
            return this;
        }

        public Builder withStartedDate(ZonedDateTime startedDate) {
            this.startedDate = Optional.ofNullable(startedDate);
            return this;
        }

        public Builder withCompletedDate(ZonedDateTime completedDate) {
            this.completedDate = Optional.ofNullable(completedDate);
            return this;
        }

        public Builder withTotalRows(Long totalRows) {
            this.totalRows = Optional.ofNullable(totalRows);
            return this;
        }

        public Builder withFormatsInfo(Map<ReportFormat, FormatReportInfoResponse> results) {
            this.formatsInfo = results;
            return this;
        }

        public PublicReportResultResponse build() {
            return new PublicReportResultResponse(status, startedDate, completedDate, totalRows, formatsInfo);
        }
    }
}
