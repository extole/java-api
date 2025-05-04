package com.extole.client.rest.shareable.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public class ClientShareableV2RestException extends ExtoleRestException {
    public static final ErrorCode<ClientShareableV2RestException> NOT_FOUND =
        new ErrorCode<>("shareable_not_found", 403, "Shareable id not found", "shareable_id");

    public ClientShareableV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
