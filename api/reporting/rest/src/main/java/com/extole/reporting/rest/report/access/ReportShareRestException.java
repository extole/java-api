package com.extole.reporting.rest.report.access;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportShareRestException extends ExtoleRestException {

    public static final ErrorCode<ReportShareRestException> INVALID_DURATION =
        new ErrorCode<>("invalid_duration", 400,
            "The requested duration for this token must not be negative and be end within the first ten millenium",
            "duration_seconds");
    public static final ErrorCode<ReportShareRestException> NO_SUCH_RESOURCE =
        new ErrorCode<>("no_such_resource", 400, "The resource could not be found.");

    public ReportShareRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
