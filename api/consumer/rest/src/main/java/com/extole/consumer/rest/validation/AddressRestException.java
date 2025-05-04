package com.extole.consumer.rest.validation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AddressRestException extends ExtoleRestException {

    public static final ErrorCode<AddressRestException> VALIDATION_FAILED =
        new ErrorCode<>("address_validation", 400, "Failed to validate address.", "details");

    public AddressRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
