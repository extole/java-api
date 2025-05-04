package com.extole.reporting.rest.audience.operation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceOperationRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceOperationRestException> AUDIENCE_NOT_FOUND =
        new ErrorCode<>("audience_operation_audience_not_found", 400, "Audience was not found", "audience_id");

    public static final ErrorCode<AudienceOperationRestException> OPERATION_NOT_FOUND =
        new ErrorCode<>("audience_operation_operation_not_found", 400, "Operation was not found", "operation_id",
            "audience_id");

    public static final ErrorCode<AudienceOperationRestException> ANOTHER_OPERATION_IN_PROGRESS =
        new ErrorCode<>("audience_operation_another_operation_in_progress", 400,
            "Another operation in progress was found", "operation_id",
            "audience_id");

    public AudienceOperationRestException(String uniqueId, ErrorCode<AudienceOperationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
