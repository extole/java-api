package com.extole.client.rest.webhook.reward;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardWebhookRestException extends ExtoleRestException {

    public static final ErrorCode<RewardWebhookRestException> REWARD_WEBHOOK_NOT_FOUND = new ErrorCode<>(
        "reward_webhook_not_found", 400, "Reward webhook is not found", "webhook_id");

    public RewardWebhookRestException(String uniqueId, ErrorCode<RewardWebhookRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
