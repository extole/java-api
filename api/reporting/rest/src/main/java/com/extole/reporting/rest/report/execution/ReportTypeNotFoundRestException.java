package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportTypeNotFoundRestException extends ExtoleRestException {
    public static final ErrorCode<ReportTypeNotFoundRestException> REPORT_TYPE_NOT_FOUND =
        new ErrorCode<>("report_type_not_found", 400, "Report type not found", "report_type");

    public ReportTypeNotFoundRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
