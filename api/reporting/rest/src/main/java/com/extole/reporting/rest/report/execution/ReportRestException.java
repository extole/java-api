package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportRestException extends ExtoleRestException {

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_NOT_FOUND =
        new ErrorCode<>("report_content_not_found", 400, "Report content not found", "report_id");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_NOT_FOUND_FOR_TAGS =
        new ErrorCode<>("report_content_by_tags_not_found", 400, "Report content by tags not found",
            "having_any_tags", "having_all_tags", "exclude_having_any_tags", "exclude_having_all_tags");

    public static final ErrorCode<ReportRestException> REPORT_FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("report_content_not_supported", 400, "Report format not supported", "report_id",
            "format");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_TYPE_NOT_SUPPORTED =
        new ErrorCode<>("report_content_not_supported", 400, "Report content type not supported", "report_id",
            "content_type");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_FORMAT_NOT_FOUND =
        new ErrorCode<>("report_content_format_not_found", 400, "Report content format not found", "report_id",
            "format");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_FORMAT_NOT_FOUND_FOR_TAGS =
        new ErrorCode<>("report_content_format_by_tags_not_found", 400, "Report content format by tags not found",
            "having_any_tags", "having_all_tags", "exclude_having_any_tags", "exclude_having_all_tags",
            "format");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_NOT_DOWNLOADED =
        new ErrorCode<>("report_content_not_found", 400, "Report content could not be downloaded", "report_id");

    public static final ErrorCode<ReportRestException> REPORT_CONTENT_NOT_DOWNLOADED_FOR_TAGS =
        new ErrorCode<>("report_content_by_tags_not_found", 400, "Report content by tags could not be downloaded",
            "having_any_tags", "having_all_tags", "exclude_having_any_tags", "exclude_having_all_tags");

    public static final ErrorCode<ReportRestException> REPORT_ACCESS_DENIED =
        new ErrorCode<>("access_denied", 403,
            "The access_token provided is not permitted to access the specified resource.");

    public static final ErrorCode<ReportRestException> LATEST_REPORT_NOT_FOUND =
        new ErrorCode<>("latest_report_not_found", 404, "Latest Report not found");

    public static final ErrorCode<ReportRestException> REPORT_FILTER_MISSING =
        new ErrorCode<>("report_filter_missing", 400, "Report tags is required");

    public ReportRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
