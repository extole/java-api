package com.extole.client.rest.person.step.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonStepV2RestException extends ExtoleRestException {

    public static final ErrorCode<PersonStepV2RestException> STEP_NOT_FOUND =
        new ErrorCode<>("person_step_not_found", 400, "Person step was not found", "person_id", "step_id");

    public PersonStepV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
