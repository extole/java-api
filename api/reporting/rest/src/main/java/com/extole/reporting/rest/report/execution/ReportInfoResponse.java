package com.extole.reporting.rest.report.execution;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportInfoResponse {
    private static final String TOTAL_ROWS = "total_rows";
    private final long totalRows;

    public ReportInfoResponse(@JsonProperty(TOTAL_ROWS) long totalRows) {
        this.totalRows = totalRows;
    }

    @JsonProperty(TOTAL_ROWS)
    public long getTotalRows() {
        return totalRows;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
