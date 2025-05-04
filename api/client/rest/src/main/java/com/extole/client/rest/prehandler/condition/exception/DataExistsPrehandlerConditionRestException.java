package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class DataExistsPrehandlerConditionRestException extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<DataExistsPrehandlerConditionRestException> PREHANDLER_CONDITION_DATA_IS_MISSING =
        new ErrorCode<>("prehandler_condition_data_is_missing", 400, "Prehandler condition is missing data");

    public static final ErrorCode<DataExistsPrehandlerConditionRestException> PREHANDLER_CONDITION_DATA_INVALID =
        new ErrorCode<>("prehandler_condition_data_invalid", 400, "Prehandler condition data invalid",
            "params");

    public DataExistsPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
