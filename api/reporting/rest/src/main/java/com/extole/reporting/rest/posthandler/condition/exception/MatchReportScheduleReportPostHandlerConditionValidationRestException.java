package com.extole.reporting.rest.posthandler.condition.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;

public class MatchReportScheduleReportPostHandlerConditionValidationRestException
    extends ReportPostHandlerConditionValidationRestException {

    public static final ErrorCode<
        MatchReportScheduleReportPostHandlerConditionValidationRestException> REPORT_SCHEDULE_ID_MISSING =
            new ErrorCode<>("report_post_handler_report_schedule_id_missing", 400, "report_schedule_id_missing");

    public static final ErrorCode<
        MatchReportScheduleReportPostHandlerConditionValidationRestException> REPORT_SCHEDULE_NOT_FOUND =
            new ErrorCode<>("report_post_handler_report_schedule_not_found", 400, "report_schedule_not_found", "id");

    public MatchReportScheduleReportPostHandlerConditionValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
