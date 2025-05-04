package com.extole.reporting.rest.report.runner;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class RefreshingReportRunnerValidationRestException extends ReportRunnerValidationRestException {

    public static final ErrorCode<RefreshingReportRunnerValidationRestException> REPORT_RUNNER_INVALID_EXPIRATION_MS =
        new ErrorCode<>("report_runner_invalid_expiration_ms", 400,
            "Report runner expiration should be greater than 0.");

    public RefreshingReportRunnerValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
