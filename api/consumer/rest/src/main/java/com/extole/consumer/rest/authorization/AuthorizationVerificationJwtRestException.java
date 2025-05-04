package com.extole.consumer.rest.authorization;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthorizationVerificationJwtRestException extends ExtoleRestException {

    public static final ErrorCode<AuthorizationVerificationJwtRestException> JWT_AUTHENTICATION_VERIFICATION_FAILED =
        new ErrorCode<>("jwt_error", 403, "The jwt authentication verification failed.", "reason", "description");

    public AuthorizationVerificationJwtRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
