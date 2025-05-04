package com.extole.client.rest.prehandler.action.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class ExpressionPrehandlerActionRestException extends PrehandlerActionValidationRestException {

    public static final ErrorCode<ExpressionPrehandlerActionRestException> PREHANDLER_ACTION_EXPRESSION_INVALID =
        new ErrorCode<>("prehandler_action_expression_invalid", 400,
            "Prehandler action expression is invalid");

    public static final ErrorCode<ExpressionPrehandlerActionRestException> PREHANDLER_ACTION_EXPRESSION_MISSING =
        new ErrorCode<>("prehandler_action_expression_missing", 400,
            "Prehandler action is missing expression");

    public ExpressionPrehandlerActionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
