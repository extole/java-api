package com.extole.client.rest.webhook.reward.filter.expression;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ExpressionRewardWebhookFilterRestException
    extends ExtoleRestException {

    public static final ErrorCode<
        ExpressionRewardWebhookFilterRestException> EXPRESSION_REWARD_WEBHOOK_FILTER_NOT_FOUND =
            new ErrorCode<>("expression_reward_webhook_filter_not_found", 400,
                "Expression Reward Webhook filter is not " + "found", "webhook_id", "filter_id");

    public static final ErrorCode<
        ExpressionRewardWebhookFilterRestException> EXPRESSION_REWARD_WEBHOOK_FILTER_BUILD_FAILED =
            new ErrorCode<>("webhook_build_failed", 400, "Webhook build failed", "webhook_id", "evaluatable_name",
                "evaluatable");

    public ExpressionRewardWebhookFilterRestException(String uniqueId,
        ErrorCode<ExpressionRewardWebhookFilterRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
