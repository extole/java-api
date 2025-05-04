package com.extole.reporting.rest.audience.operation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RetryAudienceOperationRestException extends ExtoleRestException {

    public static final ErrorCode<RetryAudienceOperationRestException> TYPE_NOT_SUPPORTED =
        new ErrorCode<>("retry_audience_operation_type_not_supported", 400, "Retry is not supported for type",
            "operation_id", "operation_type", "supported_types");

    public static final ErrorCode<RetryAudienceOperationRestException> STATE_NOT_ALLOWED =
        new ErrorCode<>("retry_audience_operation_state_not_allowed", 400, "Retry is not allowed for state",
            "operation_id", "state");

    public RetryAudienceOperationRestException(String uniqueId, ErrorCode<CancelAudienceOperationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
