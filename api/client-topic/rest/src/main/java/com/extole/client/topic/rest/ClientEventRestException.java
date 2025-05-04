package com.extole.client.topic.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientEventRestException extends ExtoleRestException {
    public static final ErrorCode<ClientEventRestException> INVALID_NAME = new ErrorCode<>(
        "invalid_name", 400, "name must be at least 2 characters", "name");
    public static final ErrorCode<ClientEventRestException> INVALID_MESSAGE = new ErrorCode<>(
        "invalid_message", 400, "message must be at least 2 characters", "message");

    public ClientEventRestException(String uniqueId, ErrorCode<ClientEventRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
