package com.extole.api.event.client;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.report_runner.ReportRunner;

@Schema
public interface ReportRunnerClientEvent extends ClientEvent {
    String getReportRunnerId();

    ReportRunner getReportRunner();

    String getOperation();
}
