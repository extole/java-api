package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupExecutionRestException extends ExtoleRestException {
    public static final ErrorCode<FixupExecutionRestException> FIXUP_EXECUTION_NOT_FOUND =
        new ErrorCode<>("fixup_execution_not_found", 400, "Fixup execution not found", "fixup_id",
            "fixup_execution_id");

    public FixupExecutionRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
