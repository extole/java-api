package com.extole.client.rest.person.memberships;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonMembershipRestException extends ExtoleRestException {

    public static final ErrorCode<PersonMembershipRestException> AUDIENCE_NOT_FOUND =
        new ErrorCode<>("audience_not_found", 400, "Audience not found", "person_id", "audience_id");

    public PersonMembershipRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
