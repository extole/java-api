package com.extole.reporting.rest.report.runner;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportRunnerQueryRestException extends ExtoleRestException {

    public static final ErrorCode<ReportRunnerQueryRestException> REPORT_RUNNER_INVALID_TYPE =
        new ErrorCode<>("report_runner_invalid_type", 400, "Report runner type is invalid", "type");
    public static final ErrorCode<ReportRunnerQueryRestException> REPORT_RUNNER_INVALID_STATUS =
        new ErrorCode<>("report_runner_invalid_status", 400, "Report runner status is invalid", "status");

    public static final ErrorCode<ReportRunnerQueryRestException> REPORT_RUNNER_INVALID_PAUSE_STATUS =
        new ErrorCode<>("report_runner_invalid_pause_status", 400, "Report runner pause status is invalid", "status");

    public static final ErrorCode<ReportRunnerQueryRestException> REPORT_RUNNER_INVALID_AGGREGATION_STATUS =
        new ErrorCode<>("report_runner_invalid_aggregation_status", 400, "Report runner aggregation status is invalid",
            "status");
    public static final ErrorCode<ReportRunnerQueryRestException> REPORT_RUNNER_INVALID_SLOT_TYPE =
        new ErrorCode<>("report_runner_invalid_slot_type", 400, "Report runner slot type is invalid", "type");

    public ReportRunnerQueryRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
