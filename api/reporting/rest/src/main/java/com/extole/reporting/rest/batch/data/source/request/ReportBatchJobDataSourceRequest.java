package com.extole.reporting.rest.batch.data.source.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

public class ReportBatchJobDataSourceRequest extends BatchJobDataSourceRequest {
    static final String DATA_SOURCE_TYPE = "REPORT";

    private static final String REPORT_ID = "report_id";

    private final String reportId;

    public ReportBatchJobDataSourceRequest(@JsonProperty(REPORT_ID) String reportId) {
        super(BatchJobDataSourceType.REPORT);
        this.reportId = reportId;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }
}
