package com.extole.common.rest.exception;

import java.util.Map;

public class RequestBodySizeTooLargeRestRuntimeException extends ExtoleRestRuntimeException {

    public static final ErrorCode<RequestBodySizeTooLargeRestRuntimeException> REQUEST_BODY_TOO_LARGE =
        new ErrorCode<>("request_body_size_too_large", 400, "Request body size too large", "body_size",
            "max_allowed_body_size");

    public RequestBodySizeTooLargeRestRuntimeException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
