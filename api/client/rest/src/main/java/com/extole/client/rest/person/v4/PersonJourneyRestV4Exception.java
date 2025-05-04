package com.extole.client.rest.person.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonJourneyRestV4Exception extends ExtoleRestException {

    public static final ErrorCode<PersonJourneyRestV4Exception> JOURNEY_NOT_FOUND =
        new ErrorCode<>("journey_not_found", 400, "Journey not found", "person_id", "journey_id");

    public PersonJourneyRestV4Exception(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
