package com.extole.reporting.rest.fixup.transformation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupTransformationRestException extends ExtoleRestException {

    public static final ErrorCode<FixupTransformationRestException> TRANSFORMATION_NOT_FOUND =
        new ErrorCode<>("fixup_transformation_not_found", 400, "Transformation not found",
            "fixup_id",
            "fixup_transformation_id");

    public static final ErrorCode<FixupTransformationRestException> NOT_EDITABLE =
        new ErrorCode<>("fixup_transformation_not_editable", 400, "Fixup transformation is not editable");

    public FixupTransformationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
