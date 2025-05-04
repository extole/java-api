package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class EventNameMatchPrehandlerConditionRestException
    extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<
        EventNameMatchPrehandlerConditionRestException> PREHANDLER_CONDITION_EVENT_NAME_MISSING =
            new ErrorCode<>("prehandler_condition_event_name_missing", 400,
                "Prehandler condition is missing event name");

    public EventNameMatchPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
