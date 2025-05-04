package com.extole.reporting.rest.report.execution;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReportListQueryRestException extends ExtoleRestException {

    public static final ErrorCode<ReportListQueryRestException> INVALID_TIMERANGE_FORMAT =
        new ErrorCode<>("invalid_timerange_format", 400, "Time range is not of a valid ISO_8601 format");

    public ReportListQueryRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
