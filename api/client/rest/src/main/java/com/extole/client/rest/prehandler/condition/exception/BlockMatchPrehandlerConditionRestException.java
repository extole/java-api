package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class BlockMatchPrehandlerConditionRestException extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<BlockMatchPrehandlerConditionRestException> EMPTY_LIST_TYPES =
        new ErrorCode<>("prehandler_condition_empty_considered_list_types", 400,
            "Considered list types cannot be empty or null");

    public BlockMatchPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
