package com.extole.client.rest.property;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PropertyCreationRestException extends ExtoleRestException {

    public static final ErrorCode<PropertyCreationRestException> DUPLICATE_PROPERTY =
        new ErrorCode<>("duplicate_property", 403, "Property already exists", "name");

    public PropertyCreationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
