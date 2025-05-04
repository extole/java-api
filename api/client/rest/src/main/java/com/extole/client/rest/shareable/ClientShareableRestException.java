package com.extole.client.rest.shareable;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientShareableRestException extends ExtoleRestException {
    public static final ErrorCode<ClientShareableRestException> SHAREABLE_NOT_FOUND =
        new ErrorCode<>("shareable_not_found", 403, "Shareable not found", "code");

    public static final ErrorCode<ClientShareableRestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 403, "Person not found", "person_id");

    public ClientShareableRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
