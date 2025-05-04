package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CrossClientReportIdFixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> REPORT_ID_INVALID =
        new ErrorCode<>("report_id_invalid", 400, "Report Id is invalid");

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> REPORT_ID_MISSING =
        new ErrorCode<>("report_id_missing", 400, "Report Id is missing");

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> CLIENT_ID_FIELD_MISSING =
        new ErrorCode<>("client_id_field_missing", 400, "Client Id field name is missing");

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> EVENT_ID_FIELD_MISSING =
        new ErrorCode<>("event_id_field_missing", 400, "Event Id field name is missing");

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> CLIENT_ID_FIELD_LENGTH =
        new ErrorCode<>("client_id_field_missing", 400, "Client Id field name is too long.");

    public static final ErrorCode<CrossClientReportIdFixupFilterValidationRestException> EVENT_ID_FIELD_LENGTH =
        new ErrorCode<>("event_id_field_missing", 400, "Event Id field name is too long.");

    public CrossClientReportIdFixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
