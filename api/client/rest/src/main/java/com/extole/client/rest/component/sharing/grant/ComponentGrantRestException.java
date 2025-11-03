package com.extole.client.rest.component.sharing.grant;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentGrantRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentGrantRestException> NOT_FOUND =
        new ErrorCode<>("grant_not_found", 404, "Components grant not found", "grant_id");

    public static final ErrorCode<ComponentGrantRestException> MISSING_CLIENT_ID = new ErrorCode<>(
        "missing_client_id", 400, "Client id is missing in the request");

    public ComponentGrantRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
