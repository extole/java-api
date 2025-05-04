package com.extole.client.rest.support;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SupportRestException extends ExtoleRestException {
    public static final ErrorCode<SupportRestException> USER_NOT_FOUND = new ErrorCode<>("user_not_found", 400,
        "User not found", "user_id");

    public SupportRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
