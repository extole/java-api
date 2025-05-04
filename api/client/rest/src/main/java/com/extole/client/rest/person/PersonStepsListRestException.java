package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonStepsListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonStepsListRestException> STEP_INVALID_VISIT_TYPE =
        new ErrorCode<>("person_step_invalid_visit_type", 400, "Person step visit type is invalid", "visit_type");

    public static final ErrorCode<PersonStepsListRestException> INVALID_FLOW_QUERY =
        new ErrorCode<>("person_step_invalid_flow_query", 400, "Person step flow query is invalid");

    public static final ErrorCode<PersonStepsListRestException> VALUE_DOES_NOT_FOLLOW_PATTERN = new ErrorCode<>(
        "value_does_not_follow_pattern", 400, "Value does not follow the key:value pattern", "parameter", "value");

    public PersonStepsListRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
