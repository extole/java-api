package com.extole.client.rest.rewards;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardRestException extends ExtoleRestException {

    public static final ErrorCode<RewardRestException> REWARD_NOT_FOUND = new ErrorCode<>(
        "reward_not_found", 400, "Reward not found", "reward_id");

    public static final ErrorCode<RewardRestException> REWARD_SUPPLIER_NOT_FOUND = new ErrorCode<>(
        "reward_supplier_not_found", 400, "Reward supplier not found", "reward_supplier_id");

    public static final ErrorCode<RewardRestException> REWARD_RETRY_NOT_SUPPORTED = new ErrorCode<>(
        "reward_retry_not_supported", 400, "Reward retry not supported", "reward_id");

    public static final ErrorCode<RewardRestException> CLAIMED_REWARD_RETRY = new ErrorCode<>(
        "retry_claimed_reward_not_allowed", 400, "Retry for Claimed Reward is not allowed", "reward_id");

    public static final ErrorCode<RewardRestException> REWARD_NOT_RETRYABLE_STATE = new ErrorCode<>(
        "reward_in_not_retryable_state", 400, "Reward is in a not retryable state", "reward_id", "current_reward_state",
        "retryable_states");

    public static final ErrorCode<RewardRestException> REWARD_ILLEGAL_STATE_TRANSITION = new ErrorCode<>(
        "reward_illegal_state_transition", 400, "Reward illegal state transition for current state", "current_state");

    public static final ErrorCode<RewardRestException> SANDBOX_NOT_FOUND = new ErrorCode<>(
        "sandbox_not_found", 400, "Unable to find sandbox", "sandbox");

    public RewardRestException(String uniqueId, ErrorCode<RewardRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
