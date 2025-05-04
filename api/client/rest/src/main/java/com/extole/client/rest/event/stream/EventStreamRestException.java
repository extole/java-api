package com.extole.client.rest.event.stream;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EventStreamRestException extends ExtoleRestException {

    public static final ErrorCode<EventStreamRestException> EVENT_STREAM_NOT_FOUND = new ErrorCode<>(
        "event_stream_not_found", 400, "EventStream was not found", "event_stream_id");

    public EventStreamRestException(String uniqueId, ErrorCode<EventStreamRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
