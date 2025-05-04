package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportNotFoundRestException extends ExtoleRestException {
    public static final ErrorCode<ReportNotFoundRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("report_not_found", 400, "Report not found", "report_id");

    public static final ErrorCode<ReportNotFoundRestException> REPORT_NOT_FOUND_FOR_SOURCE_AND_TAGS =
        new ErrorCode<>("report_not_found", 400, "Report not found", "source", "having_any_tags", "having_all_tags",
            "exclude_having_any_tags", "exclude_having_all_tags");

    public ReportNotFoundRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
