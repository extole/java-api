package com.extole.reporting.rest.report.runner;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportRunnerRestException extends ExtoleRestException {

    public static final ErrorCode<ReportRunnerRestException> REPORT_RUNNER_NOT_FOUND =
        new ErrorCode<>("report_runner_not_found", 404, "Report Runner not found", "report_runner_id");

    public static final ErrorCode<ReportRunnerRestException> LATEST_REPORT_NOT_FOUND =
        new ErrorCode<>("latest_report_not_found", 404, "Latest Report not found", "report_runner_id");

    public ReportRunnerRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
