package com.extole.client.topic.rest.snooze;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SnoozeRestException extends ExtoleRestException {
    public static final ErrorCode<SnoozeRestException> USER_NOT_FOUND =
        new ErrorCode<>("user_not_found", 400, "User not found");
    public static final ErrorCode<SnoozeRestException> INVALID_USER_ID =
        new ErrorCode<>("invalid_user_id", 400, "Invalid user id", "user_id");
    public static final ErrorCode<SnoozeRestException> INVALID_SNOOZE_ID =
        new ErrorCode<>("invalid_snooze_id", 400, "Invalid snooze id", "snooze_id");

    public SnoozeRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
