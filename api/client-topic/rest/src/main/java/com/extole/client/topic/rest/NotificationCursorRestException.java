package com.extole.client.topic.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class NotificationCursorRestException extends ExtoleRestException {

    public static final ErrorCode<NotificationCursorRestException> MISSING_DATE_TIME = new ErrorCode<>(
        "missing_date_time", 400, "Date time is missing");

    public NotificationCursorRestException(String uniqueId, ErrorCode<NotificationCursorRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
