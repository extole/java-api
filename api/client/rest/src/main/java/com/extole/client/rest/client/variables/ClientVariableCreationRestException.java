package com.extole.client.rest.client.variables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientVariableCreationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientVariableCreationRestException> NAME_MISSING =
        new ErrorCode<>("variable_name_missing", 400, "Variable name is missing");

    public static final ErrorCode<ClientVariableCreationRestException> DUPLICATED_NAME =
        new ErrorCode<>("variable_name_duplicated", 400,
            "Variable names should be unique", "name");

    public static final ErrorCode<ClientVariableCreationRestException> INVALID_NAME =
        new ErrorCode<>("variable_name_invalid", 400,
            "Variable name can only contain alphanumeric and period/underscore/dash characters", "name");

    public ClientVariableCreationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
