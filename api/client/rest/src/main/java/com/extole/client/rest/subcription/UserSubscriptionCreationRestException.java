package com.extole.client.rest.subcription;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserSubscriptionCreationRestException extends ExtoleRestException {

    public static final ErrorCode<UserSubscriptionCreationRestException> DUPLICATE_USER_SUBSCRIPTION =
        new ErrorCode<>("duplicate_user_subscription", 403, "User subscription already exists", "having_all_tags",
            "filtering_level", "user_id");

    public UserSubscriptionCreationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
