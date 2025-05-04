package com.extole.client.rest.client.variables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientVariableValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientVariableValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("variable_name_length_out_of_range", 400,
            "Variable name length is out of range", "name", "min_length", "max_length");

    public static final ErrorCode<ClientVariableValidationRestException> VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("variable_value_length_out_of_range", 400,
            "Variable value length is out of range", "value", "min_length", "max_length");

    public static final ErrorCode<ClientVariableValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("variable_description_length_out_of_range", 400,
            "Variable description length is out of range", "description", "max_length");

    public ClientVariableValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
