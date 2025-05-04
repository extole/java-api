package com.extole.client.topic.rest.snooze;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SnoozeValidationRestException extends ExtoleRestException {
    public static final ErrorCode<SnoozeValidationRestException> MISSING_TAGS =
        new ErrorCode<>("missing_tags", 400, "At least one tag must be specified", "having_exactly_tags");
    public static final ErrorCode<SnoozeValidationRestException> INVALID_EXPIRES_AT =
        new ErrorCode<>("invalid_expires_at", 400, "Snooze must expire within the next thirty days", "expires_at");

    public SnoozeValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
