package com.extole.reporting.rest.audience.membership;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonMembershipValidationRestException extends ExtoleRestException {

    public static final ErrorCode<PersonMembershipValidationRestException> MISSING_AUDIENCE_ID =
        new ErrorCode<>("missing_audience_id", 400, "Audience ID is not specified");

    public PersonMembershipValidationRestException(String uniqueId,
        ErrorCode<PersonMembershipValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
