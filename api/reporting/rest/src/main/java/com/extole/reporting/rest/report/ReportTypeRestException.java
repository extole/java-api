package com.extole.reporting.rest.report;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportTypeRestException extends ExtoleRestException {

    public static final ErrorCode<ReportTypeRestException> REPORT_TYPE_NOT_FOUND =
        new ErrorCode<>("report_type_not_found", 400, "Report type not found", "id");

    public static final ErrorCode<ReportTypeRestException> MISSING_TYPE =
        new ErrorCode<>("report_type_missing", 400, "Report type is missing");

    public static final ErrorCode<ReportTypeRestException> REPORT_TYPE_HAS_DEPENDENT_TYPES =
        new ErrorCode<>("report_type_has_dependent_types", 400, "Report type has dependent types", "id",
            "dependent_report_type_ids");

    public ReportTypeRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
