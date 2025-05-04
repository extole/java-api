package com.extole.reporting.rest.audience.list;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class DynamicAudienceListValidationRestException extends ExtoleRestException {

    public static final ErrorCode<DynamicAudienceListValidationRestException> REPORT_RUNNER_ID_MISSING =
        new ErrorCode<>("dynamic_audience_list_report_runner_id_missing", 400, "ReportRunnerId is mandatory");

    public static final ErrorCode<DynamicAudienceListValidationRestException> REPORT_RUNNER_NOT_FOUND =
        new ErrorCode<>("dynamic_audience_list_report_runner_not_found", 400, "ReportRunner not found",
            "report_runner_id");

    public static final ErrorCode<DynamicAudienceListValidationRestException> REPORT_RUNNER_NOT_ACCESSIBLE =
        new ErrorCode<>("dynamic_audience_list_report_runner_not_accessible", 400,
            "ReportRunner is not accessible for client", "report_runner_id");

    public DynamicAudienceListValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
