package com.extole.client.topic.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserNotificationRestException extends ExtoleRestException {

    public static final ErrorCode<UserNotificationRestException> USER_NOT_FOUND = new ErrorCode<>(
        "user_not_found", 400, "User is not found", "user_id", "client_id", "request_uri");

    public UserNotificationRestException(String uniqueId, ErrorCode<UserNotificationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
