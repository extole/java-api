package com.extole.client.rest.rewards;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardQueryRestException extends ExtoleRestException {

    public static final ErrorCode<RewardQueryRestException> INVALID_REWARD_STATE = new ErrorCode<>(
        "reward_state_invalid", 400, "Specified reward state type is not supported", "reward_state_type");

    public static final ErrorCode<RewardQueryRestException> UNSUPPORTED_REWARD_TYPE = new ErrorCode<>(
        "reward_type_not_supported", 400, "Reward type not supported", "reward_type");

    public static final ErrorCode<RewardQueryRestException> UNSUPPORTED_PERIOD = new ErrorCode<>(
        "period_not_supported", 400, "Period not supported");

    public static final ErrorCode<RewardQueryRestException> INVALID_PERIOD_COUNT = new ErrorCode<>(
        "period_count_invalid", 400, "Invalid period count");

    public static final ErrorCode<RewardQueryRestException> INVALID_TIME_INTERVAL = new ErrorCode<>(
        "time_interval_invalid", 400, "Time interval is invalid or not ISO-8601 explicit form");

    public static final ErrorCode<RewardQueryRestException> INVALID_TIMEZONE = new ErrorCode<>(
        "timezone_invalid", 400, "Timezone is invalid or not ISO-8601 compliant");

    public RewardQueryRestException(String uniqueId, ErrorCode<RewardQueryRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
