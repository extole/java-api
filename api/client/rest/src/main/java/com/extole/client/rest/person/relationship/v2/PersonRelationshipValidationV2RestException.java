package com.extole.client.rest.person.relationship.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRelationshipValidationV2RestException extends ExtoleRestException {

    public static final ErrorCode<PersonRelationshipValidationV2RestException> RELATIONSHIP_UPDATE_ERROR =
        new ErrorCode<>("relationship_update_error", 500, "An error occurred during person update", "person_id");

    public PersonRelationshipValidationV2RestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
