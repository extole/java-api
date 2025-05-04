package com.extole.reporting.rest.impl.report.type.mappers;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.entity.report.type.ConfiguredReportType;
import com.extole.reporting.rest.report.type.ConfiguredReportTypeResponse;

@Component
public class ConfiguredReportTypeResponseMapper implements ReportTypeResponseMapper<ConfiguredReportType> {
    private final BaseReportTypeResponseMapper baseMapper = new BaseReportTypeResponseMapper();

    @Override
    public ConfiguredReportTypeResponse toReportTypeResponse(Authorization authorization, ZoneId clientTimezone,
        ConfiguredReportType reportType) {
        ConfiguredReportTypeResponse.Builder builder = ConfiguredReportTypeResponse.builder();
        baseMapper.applyRequestedChanges(authorization, clientTimezone, reportType, builder);
        builder.withCreatedDate(reportType.getCreatedDate().atZone(clientTimezone))
            .withUpdateDate(reportType.getUpdatedDate().atZone(clientTimezone));

        return builder.build();
    }

    @Override
    public Type getType() {
        return Type.CONFIGURED;
    }
}
