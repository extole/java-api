package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonDataRestException extends ExtoleRestException {
    public static final ErrorCode<PersonDataRestException> PERSON_DATA_NOT_FOUND =
        new ErrorCode<>("data_not_found", 400, "Data not found", "name");

    public static final ErrorCode<PersonDataRestException> DATA_ALREADY_EXISTS =
        new ErrorCode<>("data_already_exists", 400, "Data already exists", "name");

    public static final ErrorCode<PersonDataRestException> DATA_NAME_READONLY =
            new ErrorCode<>("read_only_name", 400, "One or more names supplied are read only", "name");

    public PersonDataRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
