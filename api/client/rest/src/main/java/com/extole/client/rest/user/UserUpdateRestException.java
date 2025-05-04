package com.extole.client.rest.user;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserUpdateRestException extends ExtoleRestException {
    public static final ErrorCode<UserUpdateRestException> DUPLICATE_USER =
        new ErrorCode<>("duplicate_user", 400, "User already exists", "email");

    public static final ErrorCode<UserUpdateRestException> ACCOUNT_DISABLED =
        new ErrorCode<>("account_disabled", 403, "Your account is disabled. Please contact: support@extole.com");

    public UserUpdateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
