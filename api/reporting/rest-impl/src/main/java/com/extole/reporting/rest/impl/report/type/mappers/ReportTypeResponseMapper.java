package com.extole.reporting.rest.impl.report.type.mappers;

import java.time.ZoneId;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.rest.report.type.ReportTypeResponse;

public interface ReportTypeResponseMapper<C extends ReportType> {

    ReportTypeResponse toReportTypeResponse(Authorization authorization, ZoneId clientTimezone, C reportType);

    Type getType();
}
