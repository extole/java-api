package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupFilterValidationRestException extends ExtoleRestException {

    public static final ErrorCode<FixupFilterValidationRestException> FILTER_ALREADY_EXISTS =
        new ErrorCode<>("fixup_filter_already_exists", 400, "Filter for fixup already exists", "fixup_id");

    public FixupFilterValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
