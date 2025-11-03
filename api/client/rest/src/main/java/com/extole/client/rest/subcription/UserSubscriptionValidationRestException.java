package com.extole.client.rest.subcription;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserSubscriptionValidationRestException extends ExtoleRestException {

    public static final ErrorCode<UserSubscriptionValidationRestException> INVALID_TAGS =
        new ErrorCode<>("invalid_tags", 400, "total length of tags cannot exceed 1024 characters", "having_all_tags");

    public static final ErrorCode<UserSubscriptionValidationRestException> INVALID_DEDUPE_DURATION =
        new ErrorCode<>("invalid_dedupe_duration", 400, "unacceptable dedupe duration",
            "dedupe_duration_ms", "min_dedupe_duration_ms", "max_dedupe_duration_ms");

    public static final ErrorCode<
        UserSubscriptionValidationRestException> INVALID_CHANNEL_TYPES_FOR_ZERO_DEDUPE_DURATION =
            new ErrorCode<>(
                "invalid_channel_types_for_zero_dedupe_duration", 400,
                "invalid channel types for 0ms dedupe duration", "invalid_channel_types");

    public UserSubscriptionValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
