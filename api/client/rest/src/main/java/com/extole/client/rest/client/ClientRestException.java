package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientRestException extends ExtoleRestException {

    public static final ErrorCode<ClientRestException> INVALID_CLIENT_ID =
        new ErrorCode<>("invalid_client_id", 400, "The provided client id was invalid", "client_id");

    public ClientRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
