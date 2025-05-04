package com.extole.event.api.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EventDispatcherRestException extends ExtoleRestException {
    public static final ErrorCode<EventDispatcherRestException> MISSING_EVENT_NAME =
        new ErrorCode<>("missing_event_name", 400, "Missing event name");

    public static final ErrorCode<EventDispatcherRestException> INVALID_EVENT_TIME_FORMAT =
        new ErrorCode<>("invalid_event_time_format", 400, "Invalid event time format. Expected: ISO8601 format",
            "event_time");

    public static final ErrorCode<EventDispatcherRestException> INVALID_TIME_ZONE =
        new ErrorCode<>("invalid_time_zone", 400, "Invalid time zone.", "client_id", "time_zone");

    public EventDispatcherRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
