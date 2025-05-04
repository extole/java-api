package com.extole.client.rest.webhook.reward.filter.state;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class StateRewardWebhookFilterRestException extends ExtoleRestException {

    public static final ErrorCode<StateRewardWebhookFilterRestException> STATE_REWARD_WEBHOOK_FILTER_NOT_FOUND =
        new ErrorCode<>(
            "state_reward_webhook_filter_not_found", 400, "State Reward webhook Filter is not found",
            "webhook_id", "filter_id");

    public static final ErrorCode<StateRewardWebhookFilterRestException> STATE_REWARD_WEBHOOK_FILTER_MISSING_STATES =
        new ErrorCode<>(
            "state_reward_webhook_filter_missing_states", 400,
            "State Reward webhook Filter should specify at least one state");

    public static final ErrorCode<StateRewardWebhookFilterRestException> STATE_REWARD_WEBHOOK_FILTER_BUILD_FAILED =
        new ErrorCode<>("webhook_build_failed", 400, "Webhook build failed",
            "webhook_id", "evaluatable_name", "evaluatable");

    public StateRewardWebhookFilterRestException(String uniqueId,
        ErrorCode<StateRewardWebhookFilterRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
