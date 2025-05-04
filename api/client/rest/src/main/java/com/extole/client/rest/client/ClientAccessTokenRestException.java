package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientAccessTokenRestException extends ExtoleRestException {

    public static final ErrorCode<ClientAccessTokenRestException> NOT_FOUND =
        new ErrorCode<>("access_token_not_found", 403, "Could not find client access token");

    public ClientAccessTokenRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
