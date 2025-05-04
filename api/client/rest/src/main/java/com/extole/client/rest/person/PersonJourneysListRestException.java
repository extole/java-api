package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonJourneysListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonJourneysListRestException> VALUE_DOES_NOT_FOLLOW_PATTERN = new ErrorCode<>(
        "value_does_not_follow_pattern", 400, "Value does not follow the key:value pattern", "parameter", "value");

    public PersonJourneysListRestException(String uniqueId, ErrorCode<PersonJourneysListRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
