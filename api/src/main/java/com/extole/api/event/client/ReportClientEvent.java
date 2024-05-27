package com.extole.api.event.client;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.report.Report;

@Schema
public interface ReportClientEvent extends ClientEvent {
    String getReportId();

    Report getReport();

    String getOperation();
}
