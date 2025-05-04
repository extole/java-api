package com.extole.client.rest.user;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserRestException extends ExtoleRestException {
    public static final ErrorCode<UserRestException> INVALID_USER_ID =
        new ErrorCode<>("invalid_user_id", 400, "Invalid user id", "user_id");
    public static final ErrorCode<UserRestException> INVALID_SCOPE =
        new ErrorCode<>("invalid_scope", 403, "Invalid scope", "scope");

    public UserRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
