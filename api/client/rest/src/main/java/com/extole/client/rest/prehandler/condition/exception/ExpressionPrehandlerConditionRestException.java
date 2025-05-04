package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class ExpressionPrehandlerConditionRestException extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<ExpressionPrehandlerConditionRestException> PREHANDLER_CONDITION_EXPRESSION_INVALID =
        new ErrorCode<>("prehandler_condition_expression_invalid", 400,
            "Prehandler condition has an invalid expression");

    public static final ErrorCode<ExpressionPrehandlerConditionRestException> PREHANDLER_CONDITION_EXPRESSION_MISSING =
        new ErrorCode<>("prehandler_condition_expression_missing", 400,
            "Prehandler condition is missing expression");

    public ExpressionPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
