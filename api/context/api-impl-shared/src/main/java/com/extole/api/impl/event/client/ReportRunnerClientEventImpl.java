package com.extole.api.impl.event.client;

import com.extole.api.event.client.ReportRunnerClientEvent;
import com.extole.api.impl.report_runner.RefreshingReportRunnerImpl;
import com.extole.api.impl.report_runner.ScheduledReportRunnerImpl;
import com.extole.api.report_runner.ReportRunner;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.ClientChangeEvent;
import com.extole.reporting.entity.report.runner.RefreshingReportRunner;
import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.entity.report.runner.ScheduledReportRunner;
import com.extole.reporting.pojo.report.runner.ReportRunnerPojo;

public final class ReportRunnerClientEventImpl extends ClientEventImpl implements ReportRunnerClientEvent {

    private final String reportRunnerId;
    private final ReportRunner reportRunner;
    private final String operation;

    private ReportRunnerClientEventImpl(ClientChangeEvent<ReportRunnerPojo> reportRunnerEvent) {
        super(reportRunnerEvent);
        this.reportRunnerId = reportRunnerEvent.getObjectId().getValue();
        if (ReportRunnerType.SCHEDULED.equals(reportRunnerEvent.getPojo().getType())) {
            this.reportRunner = new ScheduledReportRunnerImpl((ScheduledReportRunner) reportRunnerEvent.getPojo());
        } else {
            this.reportRunner = new RefreshingReportRunnerImpl((RefreshingReportRunner) reportRunnerEvent.getPojo());
        }
        this.operation = reportRunnerEvent.getOperation().name();
    }

    public static ReportRunnerClientEventImpl newInstance(ClientChangeEvent<ReportRunnerPojo> reportRunnerEvent) {
        return new ReportRunnerClientEventImpl(reportRunnerEvent);
    }

    @Override
    public String getReportRunnerId() {
        return reportRunnerId;
    }

    @Override
    public ReportRunner getReportRunner() {
        return reportRunner;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
