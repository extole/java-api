package com.extole.client.rest.schedule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ScheduleCancelRestException extends ExtoleRestException {
    public static final ErrorCode<ScheduleCancelRestException> ILLEGAL_STATE_CHANGE =
        new ErrorCode<>("illegal_state_change", 400, "Scheduled task is in a state that cannot be cancelled");

    public ScheduleCancelRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
