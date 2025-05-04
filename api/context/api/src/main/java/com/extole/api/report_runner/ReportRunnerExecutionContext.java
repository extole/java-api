package com.extole.api.report_runner;

import com.extole.api.model.ReportRunner;
import com.extole.api.report.configurable.TimeRange;
import com.extole.api.service.GlobalServices;

public interface ReportRunnerExecutionContext {

    ReportRunner getReportRunner();

    GlobalServices getGlobalServices();

    TimeRange getExecutionTimeSlot();
}
