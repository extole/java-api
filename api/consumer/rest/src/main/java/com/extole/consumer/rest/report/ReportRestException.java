package com.extole.consumer.rest.report;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportRestException extends ExtoleRestException {
    public static final ErrorCode<ReportRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("report_not_found", 400, "Report not found", "query");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_NOT_DOWNLOADED =
        new ErrorCode<>("report_content_not_found", 400, "Report content could not be downloaded", "report_id");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_LENGTH_EXCEEDED =
        new ErrorCode<>("report_content_not_found", 400, "Report content exceed max size", "report_id");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_TYPE_NOT_SUPPORTED =
        new ErrorCode<>("report_content_not_supported", 400, "Report content type not supported", "report_id",
            "content_type");

    public static final ErrorCode<ReportRestException> REPORT_FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("report_content_not_supported", 400, "Report format not supported", "report_id",
            "format");

    public ReportRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
