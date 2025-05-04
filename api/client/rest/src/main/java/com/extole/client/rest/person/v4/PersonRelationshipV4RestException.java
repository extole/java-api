package com.extole.client.rest.person.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRelationshipV4RestException extends ExtoleRestException {

    public static final ErrorCode<PersonRelationshipV4RestException> INVALID_ROLE =
        new ErrorCode<>("invalid_role", 400, "Invalid role", "role");

    public PersonRelationshipV4RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
