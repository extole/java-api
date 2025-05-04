package com.extole.client.rest.webhook.reward.filter;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardWebhookFilterRestException extends ExtoleRestException {

    public static final ErrorCode<RewardWebhookFilterRestException> REWARD_WEBHOOK_FILTER_UNKNOWN_TYPE =
        new ErrorCode<>(
            "unknown_reward_webhook_filter_type", 400, "Specified reward webhook filter type is unknown", "type");

    public static final ErrorCode<RewardWebhookFilterRestException> REWARD_WEBHOOK_FILTER_NOT_FOUND = new ErrorCode<>(
        "reward_webhook_filter_not_found", 400, "Reward webhook filter is not found",
        "webhook_id", "filter_id");

    public RewardWebhookFilterRestException(String uniqueId, ErrorCode<RewardWebhookFilterRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
