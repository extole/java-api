package com.extole.client.rest.component.sharing.subscription;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentSubscriptionRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentSubscriptionRestException> NOT_FOUND =
        new ErrorCode<>("subscription_not_found", 404, "Components subscription not found", "subscription_id");

    public static final ErrorCode<ComponentSubscriptionRestException> MISSING_CLIENT_ID = new ErrorCode<>(
        "missing_client_id", 400, "Client id is missing in the request");

    public static final ErrorCode<ComponentSubscriptionRestException> GRANT_REQUIRED =
        new ErrorCode<>("missing_required_grant", 400,
            "Components subscription requires a valid grant from counterpart client");

    public ComponentSubscriptionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
