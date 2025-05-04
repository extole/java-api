package com.extole.client.rest.event.stream;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EventStreamFilterRestException
    extends ExtoleRestException {

    public static final ErrorCode<EventStreamFilterRestException> EVENT_STREAM_FILTER_NOT_FOUND = new ErrorCode<>(
        "event_stream_filter_not_found", 400, "EventStream filter was not found", "event_stream_id", "filter_id");

    public static final ErrorCode<EventStreamFilterRestException> EVENT_STREAM_FILTER_VALIDATION_EXCEPTION =
        new ErrorCode<>("event_stream_filter_validation_exception", 400,
            "Filter validation failed", "event_stream_id", "filter_id", "message");

    public EventStreamFilterRestException(String uniqueId, ErrorCode<EventStreamFilterRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
