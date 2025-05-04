package com.extole.reporting.rest.posthandler.action.exception;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.posthandler.ReportPostHandlerActionValidationRestException;

public class JavascriptReportPostHandlerActionValidationRestException
    extends ReportPostHandlerActionValidationRestException {

    public static final ErrorCode<JavascriptReportPostHandlerActionValidationRestException> JAVASCRIPT_ACTION_MISSING =
        new ErrorCode<>("report_post_handler_javascript_action_missing", 400, "Javascript action missing");

    public static final ErrorCode<JavascriptReportPostHandlerActionValidationRestException> JAVASCRIPT_ACTION_INVALID =
        new ErrorCode<>("report_post_handler_javascript_action_invalid", 400, "Javascript action invalid",
            "validation_errors");

    public JavascriptReportPostHandlerActionValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
