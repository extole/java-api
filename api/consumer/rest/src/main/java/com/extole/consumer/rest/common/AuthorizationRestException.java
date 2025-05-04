package com.extole.consumer.rest.common;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthorizationRestException extends ExtoleRestException {

    public static final ErrorCode<AuthorizationRestException> ACCESS_TOKEN_MISSING =
        new ErrorCode<>("missing_access_token", 403,
            "No access_token was provided with this request.");
    public static final ErrorCode<AuthorizationRestException> JWT_AUTHENTICATION_FAILED =
        new ErrorCode<>("jwt_authentication_error", 403, "The jwt authentication failed.", "reason", "description");
    public static final ErrorCode<AuthorizationRestException> ACCESS_TOKEN_INVALID =
        new ErrorCode<>("invalid_access_token", 403,
            "The access_token provided with this request is invalid.");
    public static final ErrorCode<AuthorizationRestException> ACCESS_TOKEN_EXPIRED =
        new ErrorCode<>("expired_access_token", 403,
            "The access_token provided with this request has expired.");
    public static final ErrorCode<AuthorizationRestException> ACCESS_DENIED =
        new ErrorCode<>("access_denied", 403,
            "The access_token provided is not permitted to access the specified resource.");

    public AuthorizationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
