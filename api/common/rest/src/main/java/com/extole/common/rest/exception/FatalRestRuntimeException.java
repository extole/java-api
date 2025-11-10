package com.extole.common.rest.exception;

import java.util.Map;

public class FatalRestRuntimeException extends ExtoleRestRuntimeException {
    public static final ErrorCode<FatalRestRuntimeException> SOFTWARE_ERROR =
        new ErrorCode<>("software_error", 500, "Unexpected Software Error");

    public FatalRestRuntimeException(String uniqueId, ErrorCode<FatalRestRuntimeException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
