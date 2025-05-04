package com.extole.reporting.rest.audience.operation.action.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceValidationRestException;

public class ActionAudienceOperationDataSourceValidationRestException
    extends AudienceOperationDataSourceValidationRestException {

    public static final ErrorCode<ActionAudienceOperationDataSourceValidationRestException> MISSING_EVENT_NAME =
        new ErrorCode<>("action_audience_operation_action_data_source_missing_event_name", 400,
            "Event name is missing");

    public ActionAudienceOperationDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
