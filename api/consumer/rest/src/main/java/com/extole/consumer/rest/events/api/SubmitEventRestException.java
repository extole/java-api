package com.extole.consumer.rest.events.api;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SubmitEventRestException extends ExtoleRestException {

    public static final ErrorCode<SubmitEventRestException> MISSING_EVENT_NAME = new ErrorCode<>(
        "missing_event_name", 400, "Event name is required.");

    public static final ErrorCode<SubmitEventRestException> INVALID_EVENT_NAME = new ErrorCode<>(
        "invalid_event_name", 400, "Event name not valid.");

    public SubmitEventRestException(String uniqueId, ErrorCode<SubmitEventRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
