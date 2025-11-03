package com.extole.reporting.rest.impl.report.type.mappers;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.model.entity.report.type.ReportType.Type;
import com.extole.model.entity.report.type.SqlReportType;
import com.extole.reporting.rest.report.sql.SqlReportTypeDatabase;
import com.extole.reporting.rest.report.type.SqlReportTypeResponse;

@Component
public class SqlReportTypeResponseMapper implements ReportTypeResponseMapper<SqlReportType> {
    private final BaseReportTypeResponseMapper baseMapper = new BaseReportTypeResponseMapper();

    @Override
    public SqlReportTypeResponse toReportTypeResponse(Authorization authorization, ZoneId clientTimezone,
        SqlReportType reportType) {
        SqlReportTypeResponse.Builder builder = SqlReportTypeResponse.builder();
        baseMapper.applyRequestedChanges(authorization, clientTimezone, reportType, builder);
        return builder
            .withDatabase(SqlReportTypeDatabase.valueOf(reportType.getDatabase().name()))
            .withQuery(reportType.getQuery())
            .withCreatedDate(reportType.getCreatedDate().atZone(clientTimezone))
            .withUpdateDate(reportType.getUpdatedDate().atZone(clientTimezone))
            .build();
    }

    @Override
    public Type getType() {
        return Type.SQL;
    }
}
