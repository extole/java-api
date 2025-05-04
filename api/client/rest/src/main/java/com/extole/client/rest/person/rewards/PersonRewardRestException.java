package com.extole.client.rest.person.rewards;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRewardRestException extends ExtoleRestException {

    public static final ErrorCode<PersonRewardRestException> REWARD_NOT_FOUND =
        new ErrorCode<>("reward_not_found", 400, "Reward not found", "person_id", "reward_id");

    public PersonRewardRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
