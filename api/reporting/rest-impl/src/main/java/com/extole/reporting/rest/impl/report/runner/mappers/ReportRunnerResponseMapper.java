package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import com.extole.authorization.service.Authorization;
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.report.runner.ReportRunnerResponse;

public interface ReportRunnerResponseMapper<C extends ReportRunner> {

    ReportRunnerResponse toReportRunner(Authorization authorization, C reportRunner, ZoneId timezone);

    ReportRunnerType getType();
}
