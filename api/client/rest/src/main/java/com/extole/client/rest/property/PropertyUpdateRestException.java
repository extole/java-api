package com.extole.client.rest.property;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PropertyUpdateRestException extends ExtoleRestException {

    public static final ErrorCode<PropertyUpdateRestException> DUPLICATE_PROPERTY =
        new ErrorCode<>("duplicate_property", 403, "Property already exists", "name");

    public PropertyUpdateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
