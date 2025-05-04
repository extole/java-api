package com.extole.api.impl.report;

import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.api.report.ReportResult;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public class ReportResultImpl implements ReportResult {

    private final Status status;
    private final long totalRows;
    private final String createdDate;
    private final Optional<String> startedDate;
    private final Optional<String> completedDate;

    public ReportResultImpl(com.extole.reporting.entity.report.ReportResult reportResult) {
        this.status = Status.valueOf(reportResult.getStatus().name());
        this.totalRows = reportResult.getTotalRows();
        this.createdDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(reportResult.getCreatedDate());
        this.startedDate = reportResult.getStartedDate().map(ExtoleDateTimeFormatters.ISO_INSTANT::format);
        this.completedDate = reportResult.getCompletedDate().map(ExtoleDateTimeFormatters.ISO_INSTANT::format);
    }

    @Override
    public Object[] getData(int offset, int limit) {
        return new Object[] {};
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public long getTotalRows() {
        return totalRows;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getStartedDate() {
        return startedDate.orElse(null);
    }

    @Nullable
    @Override
    public String getCompletedDate() {
        return completedDate.orElse(null);
    }

}
