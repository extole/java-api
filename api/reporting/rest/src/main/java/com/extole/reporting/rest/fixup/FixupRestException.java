package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupRestException extends ExtoleRestException {
    public static final ErrorCode<FixupRestException> FIXUP_NOT_FOUND =
        new ErrorCode<>("fixup_not_found", 400, "Fixup not found", "fixup_id");

    public FixupRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
