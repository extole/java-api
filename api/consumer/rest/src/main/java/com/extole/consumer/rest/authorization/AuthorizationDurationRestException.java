package com.extole.consumer.rest.authorization;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthorizationDurationRestException extends ExtoleRestException {

    public static final ErrorCode<AuthorizationDurationRestException> ACCESS_TOKEN_DURATION_INVALID =
        new ErrorCode<>("invalid_access_token_duration", 400,
            "The duration provided with this request is invalid.");

    public AuthorizationDurationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
