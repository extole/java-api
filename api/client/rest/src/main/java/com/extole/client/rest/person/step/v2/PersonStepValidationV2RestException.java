package com.extole.client.rest.person.step.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonStepValidationV2RestException extends ExtoleRestException {

    public static final ErrorCode<PersonStepValidationV2RestException> PERSON_STEP_UPDATE_ERROR =
        new ErrorCode<>("person_step_update_error", 500, "An error occurred during person update", "person_id",
            "step_id");

    public PersonStepValidationV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
