package com.extole.client.rest.prehandler.action.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class SetSandboxPrehandlerActionRestException extends PrehandlerActionValidationRestException {

    public static final ErrorCode<SetSandboxPrehandlerActionRestException> PREHANDLER_ACTION_SANDBOX_MISSING =
        new ErrorCode<>("prehandler_action_sandbox_missing", 400,
            "Prehandler action is missing sandbox");

    public static final ErrorCode<SetSandboxPrehandlerActionRestException> PREHANDLER_ACTION_SANDBOX_INVALID =
        new ErrorCode<>("prehandler_action_sandbox_invalid", 400,
            "Prehandler action sandbox is invalid", "sandbox_id");

    public SetSandboxPrehandlerActionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
