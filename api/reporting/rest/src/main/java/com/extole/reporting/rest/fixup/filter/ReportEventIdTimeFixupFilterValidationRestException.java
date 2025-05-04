package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportEventIdTimeFixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportEventIdTimeFixupFilterValidationRestException> FILTER_INVALID =
        new ErrorCode<>("fixup_filter_report_event_id_time_invalid", 400,
            "Fixup Filter ReportEventIdTime is invalid");

    public static final ErrorCode<ReportEventIdTimeFixupFilterValidationRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("fixup_filter_report_event_id_time_report_not_found", 400, "Report not found");

    public ReportEventIdTimeFixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
