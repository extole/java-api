package com.extole.reporting.rest.fixup.transformation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ScriptFixupTransformationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ScriptFixupTransformationValidationRestException> TRANSFORMATION_SCRIPT_INVALID =
        new ErrorCode<>("transformation_script_invalid", 400, "Transformation Script is invalid");

    public ScriptFixupTransformationValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
