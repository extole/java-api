package com.extole.common.rest.expression;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EvaluatableRestException extends ExtoleRestException {
    private static final String DESCRIPTION = "Expression has invalid syntax and/or contains forbidden statements";

    public static final ErrorCode<EvaluatableRestException> SPEL_EXPRESSION_INVALID_SYNTAX =
        new ErrorCode<>("spel_expression_invalid_syntax", 400,
            DESCRIPTION, "description");

    public static final ErrorCode<EvaluatableRestException> HANDLEBARS_EXPRESSION_INVALID_SYNTAX =
        new ErrorCode<>("handlebars_expression_invalid_syntax", 400,
            DESCRIPTION, "description");

    public static final ErrorCode<EvaluatableRestException> JAVASCRIPT_EXPRESSION_INVALID_SYNTAX =
        new ErrorCode<>("javascript_expression_invalid_syntax", 400,
            DESCRIPTION, "description");

    public EvaluatableRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
