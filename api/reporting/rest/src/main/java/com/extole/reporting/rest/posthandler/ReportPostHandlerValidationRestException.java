package com.extole.reporting.rest.posthandler;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportPostHandlerValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ReportPostHandlerValidationRestException> EMPTY_ACTIONS =
        new ErrorCode<>("report_post_handler_empty_actions", 400, "At least one action is required");

    public static final ErrorCode<ReportPostHandlerValidationRestException> EMPTY_CONDITIONS =
        new ErrorCode<>("report_post_handler_empty_conditions", 400, "At least one condition is required");

    public static final ErrorCode<ReportPostHandlerValidationRestException> NAME_DUPLICATED =
        new ErrorCode<>("report_post_handler_name_duplicated", 400,
            "ReportPostHandler with this name already exists for the current client", "name");

    public static final ErrorCode<ReportPostHandlerValidationRestException> NAME_MISSING =
        new ErrorCode<>("report_post_handler_name_missing", 400,
            "ReportPostHandler name is missing");

    public ReportPostHandlerValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
