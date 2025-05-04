package com.extole.reporting.rest.report.execution;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class FormatReportInfoResponse {
    private static final String CONTENT_LENGTH = "content_length";
    private static final String TOTAL_ROWS = "total_rows";

    private final long contentLength;
    private final long totalRows;

    public FormatReportInfoResponse(
        @JsonProperty(CONTENT_LENGTH) long contentLength,
        @JsonProperty(TOTAL_ROWS) long totalRows) {
        this.contentLength = contentLength;
        this.totalRows = totalRows;
    }

    @JsonProperty(CONTENT_LENGTH)
    public long getContentLength() {
        return contentLength;
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
