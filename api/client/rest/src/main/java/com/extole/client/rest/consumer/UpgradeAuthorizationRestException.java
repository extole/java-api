package com.extole.client.rest.consumer;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UpgradeAuthorizationRestException extends ExtoleRestException {

    public static final ErrorCode<UpgradeAuthorizationRestException> INVALID_CONSUMER_TOKEN =
        new ErrorCode<>("consumer_token_invalid", 400, "Provided consumer access token is not valid");

    public static final ErrorCode<UpgradeAuthorizationRestException> UPGRADE_NOT_ALLOWED =
        new ErrorCode<>("consumer_token_upgrade_not_allowed", 403, "Could not upgrade provided access token");

    public UpgradeAuthorizationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
