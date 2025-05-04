package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.entity.report.runner.ScheduledReportRunner;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerResponse;
import com.extole.reporting.rest.report.schedule.ScheduleFrequency;

@Component
public class ScheduledReportRunnerResponseMapper
    implements ReportRunnerResponseMapper<ScheduledReportRunner> {
    private final BaseReportRunnerResponseMapper baseMapper = new BaseReportRunnerResponseMapper();

    @Override
    public ScheduledReportRunnerResponse toReportRunner(Authorization authorization,
        ScheduledReportRunner reportRunner, ZoneId timezone) {
        ScheduledReportRunnerResponse.Builder builder = ScheduledReportRunnerResponse.builder();

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
