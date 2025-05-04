package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonDataValidationRestException extends ExtoleRestException {
    public static final ErrorCode<PersonDataValidationRestException> INVALID_VALUE =
        new ErrorCode<>("invalid_value", 400, "Data value is invalid", "value");

    public static final ErrorCode<PersonDataValidationRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 400, "Data name is invalid", "name");

    public static final ErrorCode<PersonDataValidationRestException> DATA_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_value_length_out_of_range", 400, "Data value is too long", "value");

    public static final ErrorCode<PersonDataValidationRestException> DATA_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("data_name_length_out_of_range", 400, "Data name is too long", "name");

    public PersonDataValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
