package com.extole.api.report_runner;

public interface ScheduledReportRunner extends ReportRunner {

    String getFrequency();

    String getScheduleStartDate();
}
