package com.extole.reporting.rest.fixup;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FixupExecutedRestException extends ExtoleRestException {

    public static final ErrorCode<FixupExecutedRestException> FIXUP_EXECUTED =
        new ErrorCode<>("fixup_executed", 400, "Fixup has one successful execution.");

    public FixupExecutedRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
