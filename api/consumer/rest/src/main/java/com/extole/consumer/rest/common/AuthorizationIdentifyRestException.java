package com.extole.consumer.rest.common;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthorizationIdentifyRestException extends ExtoleRestException {

    public static final ErrorCode<AuthorizationIdentifyRestException> EMAIL_NOT_APPLICABLE =
        new ErrorCode<>("email_not_applicable", 400, "Email attribute is not applicable for current identity key",
            "identity_key");
    public static final ErrorCode<AuthorizationIdentifyRestException> EMAIL_INVALID =
        new ErrorCode<>("invalid_email", 403, "Invalid email", "email");

    public static final ErrorCode<AuthorizationIdentifyRestException> EMAIL_MISMATCH =
        new ErrorCode<>("email_mismatch", 403, "Mismatch in specified emails", "jwt_email", "email");

    public AuthorizationIdentifyRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
