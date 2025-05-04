package com.extole.reporting.rest.fixup.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupFilterRestException extends ExtoleRestException {

    public static final ErrorCode<FixupFilterRestException> FILTER_NOT_FOUND =
        new ErrorCode<>("fixup_filter_not_found", 400, "Fixup Filter not found", "fixup_id", "fixup_filter_id");

    public FixupFilterRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
