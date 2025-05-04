package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class JavascriptPrehandlerConditionRestException
    extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<JavascriptPrehandlerConditionRestException> PREHANDLER_CONDITION_JAVASCRIPT_MISSING =
        new ErrorCode<>("prehandler_condition_javascript_missing", 400,
            "Prehandler condition is missing javascript");

    public static final ErrorCode<JavascriptPrehandlerConditionRestException> PREHANDLER_CONDITION_JAVASCRIPT_INVALID =
        new ErrorCode<>("prehandler_condition_javascript_invalid", 400,
            "Prehandler condition has an invalid javascript", "output");

    public JavascriptPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
