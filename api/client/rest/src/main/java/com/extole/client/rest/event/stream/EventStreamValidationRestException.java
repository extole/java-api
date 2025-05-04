package com.extole.client.rest.event.stream;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EventStreamValidationRestException
    extends ExtoleRestException {

    public static final ErrorCode<EventStreamValidationRestException> EVENT_STREAM_MISSING_NAME = new ErrorCode<>(
        "event_stream_missing_name", 400, "Required name is not specified");

    public static final ErrorCode<EventStreamValidationRestException> EVENT_STREAM_INVALID_NAME = new ErrorCode<>(
        "event_stream_invalid_name", 400, "Invalid name", "name");

    public static final ErrorCode<EventStreamValidationRestException> EVENT_STREAM_BUILD_FAILED =
        new ErrorCode<>("build_failed", 400, "Build failed",
            "event_stream_event_stream_id", "evaluatable_name", "evaluatable");

    public static final ErrorCode<EventStreamValidationRestException> EVENT_STREAM_FILTER_VALIDATION_FAILED =
        new ErrorCode<>("event_stream_validation_failed", 400, "Filter validation failed",
            "event_stream_id", "filter_id", "message");

    public EventStreamValidationRestException(String uniqueId, ErrorCode<EventStreamValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
