package com.extole.client.rest.prehandler.action.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class JavascriptPrehandlerActionRestException extends PrehandlerActionValidationRestException {

    public static final ErrorCode<JavascriptPrehandlerActionRestException> PREHANDLER_ACTION_JAVASCRIPT_MISSING =
        new ErrorCode<>("prehandler_action_javascript_missing", 400,
            "Prehandler action is missing javascript");

    public static final ErrorCode<JavascriptPrehandlerActionRestException> PREHANDLER_ACTION_JAVASCRIPT_INVALID =
        new ErrorCode<>("prehandler_action_javascript_invalid", 400,
            "Prehandler action javascript is invalid", "output");

    public JavascriptPrehandlerActionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
