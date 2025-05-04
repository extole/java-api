package com.extole.reporting.rest.audience.list;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class StaticAudienceListValidationRestException extends ExtoleRestException {

    public static final ErrorCode<StaticAudienceListValidationRestException> REPORT_ID_MISSING =
        new ErrorCode<>("static_audience_list_report_id_missing", 400, "ReportId is mandatory");

    public static final ErrorCode<StaticAudienceListValidationRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("static_audience_list_report_not_found", 400, "Report not found",
            "report_id");

    public static final ErrorCode<StaticAudienceListValidationRestException> REPORT_NOT_ACCESSIBLE =
        new ErrorCode<>("static_audience_list_report_not_accessible", 400, "Report is not accessible for clients",
            "report_id");

    public StaticAudienceListValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
