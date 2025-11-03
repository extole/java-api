package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.model.entity.report.runner.RefreshingReportRunner;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.report.runner.RefreshingReportRunnerResponse;

@Component
public class RefreshingReportRunnerResponseMapper
    implements ReportRunnerResponseMapper<RefreshingReportRunner> {
    private final BaseReportRunnerResponseMapper baseMapper = new BaseReportRunnerResponseMapper();

    @Override
    public RefreshingReportRunnerResponse toReportRunner(Authorization authorization,
        RefreshingReportRunner reportRunner, ZoneId timezone) {
        RefreshingReportRunnerResponse.Builder builder = RefreshingReportRunnerResponse.builder();

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
