package com.extole.client.rest.erasure;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ErasureValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ErasureValidationRestException> INVALID_ERASURE_EMAIL =
        new ErrorCode<>("invalid_erasure_email", 400, "Invalid erasure email", "email");

    public static final ErrorCode<ErasureValidationRestException> EMPTY_REQUEST =
        new ErrorCode<>("empty_request", 400, "Empty request");

    public ErasureValidationRestException(String uniqueId, ErrorCode<ErasureValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
