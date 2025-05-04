package com.extole.reporting.rest.fixup.transformation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ContainerFixupTransformationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<
        ContainerFixupTransformationValidationRestException> TRANSFORMATION_CONTAINER_INVALID =
            new ErrorCode<>("transformation_container_invalid", 400, "Transformation Container is invalid");

    public ContainerFixupTransformationValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
