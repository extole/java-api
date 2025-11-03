package com.extole.reporting.rest.impl.report.type.mappers;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportType.Type;
import com.extole.reporting.rest.report.type.SparkReportTypeResponse;

@Component
public class SparkReportTypeResponseMapper implements ReportTypeResponseMapper<ReportType> {
    private final BaseReportTypeResponseMapper baseMapper = new BaseReportTypeResponseMapper();

    @Override
    public SparkReportTypeResponse toReportTypeResponse(Authorization authorization, ZoneId clientTimezone,
        ReportType reportType) {
        SparkReportTypeResponse.Builder builder = SparkReportTypeResponse.builder();
        baseMapper.applyRequestedChanges(authorization, clientTimezone, reportType, builder);
        return builder.build();
    }

    @Override
    public Type getType() {
        return Type.SPARK;
    }
}
