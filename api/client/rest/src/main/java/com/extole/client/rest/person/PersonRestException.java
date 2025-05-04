package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRestException extends ExtoleRestException {
    public static final ErrorCode<PersonRestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 403, "Person not found", "person_id");

    public PersonRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
