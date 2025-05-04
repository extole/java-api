package com.extole.reporting.rest.audience.operation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public abstract class AudienceOperationDataSourceValidationRestException extends ExtoleRestException {

    public AudienceOperationDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
