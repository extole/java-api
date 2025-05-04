package com.extole.client.rest.subcription.channel;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public abstract class UserSubscriptionChannelValidationRestException extends ExtoleRestException {

    public UserSubscriptionChannelValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
