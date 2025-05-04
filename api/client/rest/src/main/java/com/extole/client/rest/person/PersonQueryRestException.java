package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonQueryRestException extends ExtoleRestException {

    public static final ErrorCode<PersonQueryRestException> UNSUPPORTED_REWARD_STATES = new ErrorCode<>(
        "reward_states_invalid", 400, "Reward states not supported", "reward_states");

    public static final ErrorCode<PersonQueryRestException> UNSUPPORTED_REWARD_TYPES = new ErrorCode<>(
        "reward_types_invalid", 400, "Reward types not supported", "reward_types");

    public PersonQueryRestException(String uniqueId, ErrorCode<PersonQueryRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
