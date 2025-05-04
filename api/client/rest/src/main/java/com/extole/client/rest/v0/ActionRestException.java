package com.extole.client.rest.v0;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ActionRestException extends ExtoleRestException {
    public static final ErrorCode<ActionRestException> ACTION_NOT_FOUND =
        new ErrorCode<>("action_not_found", 400, "Action not found", "action_id");

    public static final ErrorCode<ActionRestException> INVALID_ACTION_ID =
        new ErrorCode<>("invalid_action_id", 400, "Invalid action id", "action_id");

    public ActionRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
