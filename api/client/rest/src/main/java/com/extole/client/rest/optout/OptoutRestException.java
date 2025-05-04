package com.extole.client.rest.optout;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OptoutRestException extends ExtoleRestException {

    public static final ErrorCode<OptoutRestException> MISSING_EMAIL_ADDRESS = new ErrorCode<>(
        "missing_email_address", 400, "Missing email address");

    public OptoutRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
