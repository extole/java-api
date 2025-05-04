package com.extole.reporting.rest.audience.operation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CancelAudienceOperationRestException extends ExtoleRestException {

    public static final ErrorCode<CancelAudienceOperationRestException> TYPE_NOT_SUPPORTED =
        new ErrorCode<>("cancel_audience_operation_type_not_supported", 400,
            "Cancellation is not supported for type", "operation_id", "operation_type", "supported_types");

    public static final ErrorCode<CancelAudienceOperationRestException> DATA_SOURCE_TYPE_NOT_SUPPORTED =
        new ErrorCode<>("cancel_audience_operation_data_source_type_not_supported", 400,
            "Cancellation is not supported for data source type", "operation_id", "data_source_type");

    public CancelAudienceOperationRestException(String uniqueId, ErrorCode<CancelAudienceOperationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
