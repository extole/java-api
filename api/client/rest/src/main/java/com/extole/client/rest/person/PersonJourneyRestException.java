package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonJourneyRestException extends ExtoleRestException {

    public static final ErrorCode<PersonJourneyRestException> JOURNEY_NOT_FOUND =
        new ErrorCode<>("journey_not_found", 400, "Journey not found", "person_id", "journey_id");

    public PersonJourneyRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
