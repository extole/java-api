package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportIdFixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportIdFixupFilterValidationRestException> FILTER_REPORT_EVENT_ID_INVALID =
        new ErrorCode<>("filter_report_event_id_invalid", 400, "Filter ReportEventId is invalid");

    public ReportIdFixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
