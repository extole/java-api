package com.extole.client.rest.person.shareables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonShareablesListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonShareablesListRestException> INVALID_DATA_VALUE = new ErrorCode<>(
        "data_values_invalid", 400, "Data values parameter format is invalid", "data_values");

    public PersonShareablesListRestException(String uniqueId, ErrorCode<PersonShareablesListRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
