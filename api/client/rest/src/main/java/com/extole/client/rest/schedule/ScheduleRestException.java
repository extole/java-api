package com.extole.client.rest.schedule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ScheduleRestException extends ExtoleRestException {
    public static final ErrorCode<ScheduleRestException> SCHEDULE_NOT_FOUND =
        new ErrorCode<>("schedule_not_found", 400, "Invalid schedule id", "schedule_id");

    public ScheduleRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
