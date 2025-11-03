package com.extole.reporting.rest.processing_lag;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProcessingStageRestException extends ExtoleRestException {

    public static final ErrorCode<ProcessingStageRestException> PROCESSING_STAGE_NOT_SUPPORTED =
        new ErrorCode<>("processing_stage_not_supported", 404, "Processing Stage not supported",
            "processing_stage");

    public ProcessingStageRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
