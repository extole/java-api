package com.extole.reporting.rest.audience.operation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceOperationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceOperationValidationRestException> MISSING_TYPE =
        new ErrorCode<>("audience_operation_missing_type", 400, "Audience operation required type is not specified");

    public static final ErrorCode<AudienceOperationValidationRestException> MISSING_DATA_SOURCE =
        new ErrorCode<>("audience_operation_missing_data_source", 400,
            "Audience operation required data source is not specified");

    public static final ErrorCode<AudienceOperationValidationRestException> MISSING_DATA_SOURCE_TYPE =
        new ErrorCode<>("audience_operation_missing_data_source_type", 400,
            "Audience operation required data source type is not specified");

    public AudienceOperationValidationRestException(String uniqueId,
        ErrorCode<AudienceOperationValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
