package com.extole.api.impl.report_runner;

import java.time.ZoneId;

import com.extole.api.impl.service.GlobalServicesFactory;
import com.extole.api.model.ReportRunner;
import com.extole.api.report.configurable.TimeRange;
import com.extole.api.report_runner.ReportRunnerExecutionContext;
import com.extole.api.service.GlobalServices;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public class ReportRunnerExecutionContextImpl implements ReportRunnerExecutionContext {

    private final ReportRunner reportRunner;
    private final TimeRange executionTimeSlot;
    private final GlobalServices globalServices;

    public ReportRunnerExecutionContextImpl(
        Id<ClientHandle> clientId,
        ReportRunner reportRunner,
        TimeRange executionTimeSlot,
        ZoneId clientTimeZone,
        GlobalServicesFactory globalServicesFactory) {
        this.reportRunner = reportRunner;
        this.executionTimeSlot = executionTimeSlot;
        this.globalServices =
            globalServicesFactory.createBuilder(clientId, "report_runner", Id.valueOf(reportRunner.getId()),
                clientTimeZone)
                .build();

    }

    @Override
    public ReportRunner getReportRunner() {
        return reportRunner;
    }

    @Override
    public GlobalServices getGlobalServices() {
        return globalServices;
    }

    @Override
    public TimeRange getExecutionTimeSlot() {
        return executionTimeSlot;
    }

}
