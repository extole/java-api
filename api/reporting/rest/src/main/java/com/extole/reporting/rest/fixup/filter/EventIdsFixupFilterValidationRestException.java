package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EventIdsFixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<EventIdsFixupFilterValidationRestException> FILTER_EVENT_IDS_INVALID =
        new ErrorCode<>("filter_event_ids_invalid", 400, "Filter EventIds is invalid");

    public EventIdsFixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
