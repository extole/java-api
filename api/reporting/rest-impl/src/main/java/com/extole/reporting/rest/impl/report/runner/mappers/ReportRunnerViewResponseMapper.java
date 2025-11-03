package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.rest.report.runner.ReportRunnerViewResponse;
import com.extole.reporting.service.report.runner.ReportRunnerWrongTypeException;

public interface ReportRunnerViewResponseMapper<C extends ReportRunner> {

    ReportRunnerViewResponse toReportRunner(Authorization authorization, C reportRunner, ZoneId timezone)
        throws AuthorizationException, ReportRunnerWrongTypeException, ReportRunnerNotFoundException,
        ClientNotFoundException;

    ReportRunnerType getType();
}
