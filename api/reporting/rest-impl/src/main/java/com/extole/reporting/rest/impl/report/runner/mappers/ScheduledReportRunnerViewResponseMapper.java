package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.model.entity.report.runner.ScheduledReportRunner;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerViewResponse;
import com.extole.reporting.rest.report.schedule.ScheduleFrequency;
import com.extole.reporting.service.report.runner.ReportRunnerWrongTypeException;

@Component
public class ScheduledReportRunnerViewResponseMapper
    implements ReportRunnerViewResponseMapper<ScheduledReportRunner> {
    private final BaseReportRunnerViewResponseMapper baseMapper;

    @Autowired
    public ScheduledReportRunnerViewResponseMapper(BaseReportRunnerViewResponseMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public ScheduledReportRunnerViewResponse toReportRunner(Authorization authorization,
        ScheduledReportRunner reportRunner, ZoneId timezone)
        throws AuthorizationException, ReportRunnerWrongTypeException, ReportRunnerNotFoundException,
        ClientNotFoundException {
        ScheduledReportRunnerViewResponse.Builder builder = ScheduledReportRunnerViewResponse.builder();

        baseMapper.applyRequestedChanges(authorization, reportRunner, timezone, builder);
        return builder
            .withFrequency(ScheduleFrequency.valueOf(reportRunner.getFrequency().name()))
            .withScheduleStartDate(reportRunner.getScheduleStartDate().atZone(timezone))
            .withLegacySftpReportNameFormat(reportRunner.isLegacySftpReportNameFormat())
            .build();
    }

    @Override
    public ReportRunnerType getType() {
        return ReportRunnerType.SCHEDULED;
    }
}
