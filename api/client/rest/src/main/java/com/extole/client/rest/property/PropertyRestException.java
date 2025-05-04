package com.extole.client.rest.property;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PropertyRestException extends ExtoleRestException {

    public static final ErrorCode<PropertyRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 403, "Invalid property name, property not found", "name");

    public PropertyRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
