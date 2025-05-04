package com.extole.consumer.rest.me.reward;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.model.RestError;

public class ClaimRewardError extends RestError {

    public static final ErrorCode<ClaimRewardError> NO_REWARD =
        new ErrorCode<>("no_reward", 403, "You didn't meet the conditions to receive a reward", "polling_id");

    public static final ErrorCode<ClaimRewardError> SOFTWARE_ERROR =
        new ErrorCode<>("software_error", 500, "Software error", "polling_id");

    @JsonCreator
    protected ClaimRewardError(
        @JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        super(uniqueId, httpStatusCode, code, message, parameters);
    }

    public ClaimRewardError(RestError restError) {
        super(restError);
    }
}
