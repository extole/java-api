package com.extole.api.impl.event.client;

import com.extole.api.event.client.ReportClientEvent;
import com.extole.api.impl.report.ReportImpl;
import com.extole.api.report.Report;
import com.extole.common.lang.ToString;

public final class ReportClientEventImpl extends ClientEventImpl implements ReportClientEvent {

    private final String reportId;
    private final Report report;
    private final String operation;

    private ReportClientEventImpl(com.extole.event.report.client.ReportClientEvent clientEvent) {
        super(clientEvent);
        this.reportId = clientEvent.getReportId().getValue();
        this.report = new ReportImpl(clientEvent.getPojo());
        this.operation = clientEvent.getOperation().name();
    }

    public static ReportClientEventImpl newInstance(com.extole.event.report.client.ReportClientEvent clientEvent) {
        return new ReportClientEventImpl(clientEvent);
    }

    @Override
    public String getReportId() {
        return reportId;
    }

    @Override
    public Report getReport() {
        return report;
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
