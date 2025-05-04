package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportDownloadRestException extends ExtoleRestException {
    public static final ErrorCode<ReportDownloadRestException> REPORT_PREVIEW_NOT_AVAILABLE =
        new ErrorCode<>("report_preview_not_available", 400, "Report preview for requested format is not available",
            "report_id", "format");

    public ReportDownloadRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
