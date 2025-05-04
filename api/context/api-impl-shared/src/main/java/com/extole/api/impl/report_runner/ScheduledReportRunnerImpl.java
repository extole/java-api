package com.extole.api.impl.report_runner;

import com.extole.api.report_runner.ScheduledReportRunner;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public class ScheduledReportRunnerImpl extends ReportRunnerImpl implements ScheduledReportRunner {
    private final String frequency;
    private final String scheduleStartDate;

    public ScheduledReportRunnerImpl(com.extole.reporting.entity.report.runner.ScheduledReportRunner reportRunner) {
        super(reportRunner);
        this.frequency = reportRunner.getFrequency().name();
        this.scheduleStartDate = ExtoleDateTimeFormatters.ISO_INSTANT.format(reportRunner.getScheduleStartDate());
    }

    @Override
    public String getFrequency() {
        return frequency;
    }

    @Override
    public String getScheduleStartDate() {
        return scheduleStartDate;
    }
}
