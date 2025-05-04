package com.extole.consumer.rest.optout;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OptoutRestException extends ExtoleRestException {

    public static final ErrorCode<OptoutRestException> INVALID_SECURE_EMAIL =
        new ErrorCode<>("invalid_secure_email", 403, "The secure_email provided is not valid.", "secure_email");

    public OptoutRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
