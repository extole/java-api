package com.extole.client.rest.user;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserCreateRestException extends ExtoleRestException {
    public static final ErrorCode<UserCreateRestException> DUPLICATE_USER =
        new ErrorCode<>("duplicate_user", 400, "User already exists", "email");

    public static final ErrorCode<UserCreateRestException> ACCOUNT_DISABLED =
        new ErrorCode<>("account_disabled", 403, "Your account is disabled. Please contact: support@extole.com");

    public UserCreateRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
