package com.extole.common.rest.exception;

import java.util.Map;

public class TooManyRequestsRestException extends ExtoleRestException {

    public static final ErrorCode<TooManyRequestsRestException> TOO_MANY_REQUESTS =
        new ErrorCode<>("too_many_requests", 429,
            "The server is unable to process your request at the moment, please retry later.");

    public TooManyRequestsRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
