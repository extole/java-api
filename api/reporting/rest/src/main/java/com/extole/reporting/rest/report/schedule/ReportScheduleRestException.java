package com.extole.reporting.rest.report.schedule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportScheduleRestException extends ExtoleRestException {

    public static final ErrorCode<ReportScheduleRestException> REPORT_SCHEDULE_NOT_FOUND =
        new ErrorCode<>("report_schedule_not_found", 404, "Report Schedule not found", "report_schedule_id");

    public ReportScheduleRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
