package com.extole.common.rest.exception;

import java.util.Map;

public class UserAuthorizationRestException extends ExtoleRestException {

    public static final ErrorCode<UserAuthorizationRestException> ACCESS_TOKEN_MISSING =
        new ErrorCode<>("missing_access_token", 403, "No access_token was provided with this request.");

    public static final ErrorCode<UserAuthorizationRestException> ACCESS_DENIED =
        new ErrorCode<>("access_denied", 403,
            "The access_token provided is not permitted to access the specified resource.");

    public static final ErrorCode<UserAuthorizationRestException> PAYMENT_REQUIRED =
        new ErrorCode<>("payment_required", 402,
            "The access_token provided is associated with an unpaid account.");

    public UserAuthorizationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
