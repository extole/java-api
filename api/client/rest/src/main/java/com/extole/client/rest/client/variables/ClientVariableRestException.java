package com.extole.client.rest.client.variables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientVariableRestException extends ExtoleRestException {

    public static final ErrorCode<ClientVariableRestException> NOT_FOUND =
        new ErrorCode<>("variable_not_found", 400, "Variable not found", "name");

    public ClientVariableRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
