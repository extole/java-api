package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.model.entity.report.runner.RefreshingReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.rest.report.runner.RefreshingReportRunnerViewResponse;
import com.extole.reporting.service.report.runner.ReportRunnerWrongTypeException;

@Component
public class RefreshingReportRunnerViewResponseMapper
    implements ReportRunnerViewResponseMapper<RefreshingReportRunner> {
    private final BaseReportRunnerViewResponseMapper baseMapper;

    @Autowired
    public RefreshingReportRunnerViewResponseMapper(BaseReportRunnerViewResponseMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public RefreshingReportRunnerViewResponse toReportRunner(Authorization authorization,
        RefreshingReportRunner reportRunner, ZoneId timezone) throws ClientNotFoundException, AuthorizationException,
        ReportRunnerWrongTypeException, ReportRunnerNotFoundException {
        RefreshingReportRunnerViewResponse.Builder builder = RefreshingReportRunnerViewResponse.builder();

        baseMapper.applyRequestedChanges(authorization, reportRunner, timezone, builder);
        return builder
            .withExpirationMs(reportRunner.getExpirationDuration().toMillis())
            .build();
    }

    @Override
    public ReportRunnerType getType() {
        return ReportRunnerType.REFRESHING;
    }
}
