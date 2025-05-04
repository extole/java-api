package com.extole.client.rest.property;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PropertyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PropertyValidationRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 403, "Invalid property name", "name");

    public static final ErrorCode<PropertyValidationRestException> NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_name", 403, "Name must be between 2 and 255 characters", "name");

    public static final ErrorCode<PropertyValidationRestException> VALUE_TOO_LONG =
        new ErrorCode<>("invalid_length_value", 403, "Value must be under 255 characters", "value");

    public static final ErrorCode<PropertyValidationRestException> NULL_VALUE =
        new ErrorCode<>("null_value", 403, "Value cannot be null", "value");

    public PropertyValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
