package com.extole.client.rest.user;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserPasswordResetRestException extends ExtoleRestException {
    public UserPasswordResetRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

    public static final ErrorCode<UserPasswordResetRestException> EMAIL_INVALID =
        new ErrorCode<>("email_invalid", 400, "The provided email address is invalid", "email");
}
