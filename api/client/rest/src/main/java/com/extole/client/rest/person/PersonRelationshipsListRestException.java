package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRelationshipsListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonRelationshipsListRestException> INVALID_DATA_VALUE = new ErrorCode<>(
        "data_values_invalid", 400, "Data values parameter format is invalid", "data_values");

    public PersonRelationshipsListRestException(String uniqueId, ErrorCode<PersonRelationshipsListRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
