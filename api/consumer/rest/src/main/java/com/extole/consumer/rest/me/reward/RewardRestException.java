package com.extole.consumer.rest.me.reward;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardRestException extends ExtoleRestException {

    public static final ErrorCode<RewardRestException> INVALID_REWARD_ID =
        new ErrorCode<>("invalid_reward_id", 400, "Invalid reward id. Reward not found.", "reward_id");

    public static final ErrorCode<RewardRestException> INVALID_REWARD_FILTER_EXCEPTION =
        new ErrorCode<>("invalid_reward_filter", 400,
            "Invalid reward filter, one of: polling_id, reward_name or partner_event_id are required");

    public RewardRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
