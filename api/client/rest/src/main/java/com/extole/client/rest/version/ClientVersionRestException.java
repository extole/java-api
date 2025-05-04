package com.extole.client.rest.version;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientVersionRestException extends ExtoleRestException {

    public static final ErrorCode<ClientVersionRestException> UNKNOWN_CLIENT_VERSION =
        new ErrorCode<>("unknown_client_version", 400,
            "The provided client version is unknown. Expecting version between 0 and latest known version",
            "latest_known_version");

    public ClientVersionRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
