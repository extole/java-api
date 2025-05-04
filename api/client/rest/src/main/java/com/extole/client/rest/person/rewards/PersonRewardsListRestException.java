package com.extole.client.rest.person.rewards;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRewardsListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonRewardsListRestException> VALUE_DOES_NOT_FOLLOW_PATTERN = new ErrorCode<>(
        "value_does_not_follow_pattern", 400, "Value does not follow the key:value pattern", "parameter", "value");

    public PersonRewardsListRestException(String uniqueId, ErrorCode<PersonRewardsListRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
