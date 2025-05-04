package com.extole.api.impl.report_runner;

import com.extole.api.report_runner.RefreshingReportRunner;

public class RefreshingReportRunnerImpl extends ReportRunnerImpl implements RefreshingReportRunner {
    private final long expirationDuration;

    public RefreshingReportRunnerImpl(
        com.extole.reporting.entity.report.runner.RefreshingReportRunner reportRunner) {
        super(reportRunner);
        this.expirationDuration = reportRunner.getExpirationDuration().toMillis();
    }

    @Override
    public long getExpirationDuration() {
        return expirationDuration;
    }
}
