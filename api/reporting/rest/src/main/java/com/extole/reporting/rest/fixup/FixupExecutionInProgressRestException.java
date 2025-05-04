package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupExecutionInProgressRestException extends ExtoleRestException {
    public static final ErrorCode<FixupExecutionInProgressRestException> FIXUP_EXECUTION_IN_PROGRESS =
        new ErrorCode<>("fixup_execution_in_progress", 400, "Another fixup execution in progress");

    public FixupExecutionInProgressRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
