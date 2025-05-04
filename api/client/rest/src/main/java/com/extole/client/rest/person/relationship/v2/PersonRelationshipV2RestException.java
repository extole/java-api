package com.extole.client.rest.person.relationship.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRelationshipV2RestException extends ExtoleRestException {

    public static final ErrorCode<PersonRelationshipV2RestException> RELATIONSHIP_NOT_FOUND =
        new ErrorCode<>("relationship_not_found", 400, "Relationship was not found", "person_id",
            "other_person_id", "container", "role");

    public static final ErrorCode<PersonRelationshipV2RestException> INVALID_ROLE =
        new ErrorCode<>("invalid_role", 400, "Invalid role", "role");

    public PersonRelationshipV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
