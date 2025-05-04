package com.extole.reporting.rest.fixup.transformation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupTransformationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<FixupTransformationValidationRestException> TRANSFORMATION_ALREADY_EXISTS =
        new ErrorCode<>("fixup_transformation_already_exists", 400, "Transformation for fixup already exists",
            "fixup_id");

    public static final ErrorCode<FixupTransformationValidationRestException> TRANSFORMATION_VALIDATION =
        new ErrorCode<>("fixup_transformation_invalid", 400, "Transformation for fixup is not valid",
            "fixup_id");

    public FixupTransformationValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
