package com.extole.reporting.rest.batch.data.source.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

public class ReportBatchJobDataSourceResponse extends BatchJobDataSourceResponse {
    static final String DATA_SOURCE_TYPE = "REPORT";

    private static final String REPORT_ID = "report_id";

    private final String reportId;

    public ReportBatchJobDataSourceResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(REPORT_ID) String reportId) {
        super(id, BatchJobDataSourceType.REPORT);
        this.reportId = reportId;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }
}
