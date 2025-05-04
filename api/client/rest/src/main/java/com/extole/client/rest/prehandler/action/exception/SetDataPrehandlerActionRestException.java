package com.extole.client.rest.prehandler.action.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class SetDataPrehandlerActionRestException extends PrehandlerActionValidationRestException {

    public static final ErrorCode<SetDataPrehandlerActionRestException> PREHANDLER_ACTION_DATA_MISSING =
        new ErrorCode<>("prehandler_action_data_missing", 400, "Prehandler action is missing data");

    public static final ErrorCode<SetDataPrehandlerActionRestException> PREHANDLER_ACTION_DATA_NAME_INVALID =
        new ErrorCode<>("prehandler_action_data_name_invalid", 400, "Prehandler action data name is invalid", "name");

    public static final ErrorCode<SetDataPrehandlerActionRestException> PREHANDLER_ACTION_DATA_VALUE_INVALID =
        new ErrorCode<>("prehandler_action_data_value_invalid", 400, "Prehandler action data value is invalid", "name");

    public SetDataPrehandlerActionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
