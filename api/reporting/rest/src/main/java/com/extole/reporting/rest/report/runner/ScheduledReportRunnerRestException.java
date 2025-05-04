package com.extole.reporting.rest.report.runner;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ScheduledReportRunnerRestException extends ExtoleRestException {

    public static final ErrorCode<ScheduledReportRunnerRestException> REPORT_RUNNER_WRONG_TYPE =
        new ErrorCode<>("report_runner_wrong_type", 400, "Report Runner of wrong type");

    public ScheduledReportRunnerRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
