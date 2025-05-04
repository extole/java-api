package com.extole.reporting.rest.posthandler;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public abstract class ReportPostHandlerConditionValidationRestException extends ExtoleRestException {

    public ReportPostHandlerConditionValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
