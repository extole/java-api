package com.extole.reporting.rest.audience.membership;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonMembershipRestException extends ExtoleRestException {

    public static final ErrorCode<PersonMembershipRestException> AUDIENCE_NOT_FOUND = new ErrorCode<>(
        "audience_not_found", 400, "Audience was not found", "audience_id");

    public static final ErrorCode<PersonMembershipRestException> MEMBERSHIP_NOT_FOUND = new ErrorCode<>(
        "membership_not_found", 400, "Membership was not found", "person_id", "audience_id");

    public static final ErrorCode<PersonMembershipRestException> PERSON_NOT_IDENTIFIED = new ErrorCode<>(
        "person_not_identified", 400, "Person is not identified", "person_id");

    public PersonMembershipRestException(String uniqueId, ErrorCode<PersonMembershipRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
