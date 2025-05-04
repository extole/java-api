package com.extole.client.rest.subcription;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserSubscriptionRestException extends ExtoleRestException {
    public static final ErrorCode<UserSubscriptionRestException> INVALID_SUBSCRIPTION_ID =
        new ErrorCode<>("invalid_subscription_id", 400, "Invalid subscription id", "subscription_id");

    public UserSubscriptionRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
