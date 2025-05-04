package com.extole.client.rest.subcription.channel;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class SlackUserSubscriptionChannelValidationRestException
    extends UserSubscriptionChannelValidationRestException {

    public static final ErrorCode<SlackUserSubscriptionChannelValidationRestException> MISSING_WEBHOOK_URL =
        new ErrorCode<>("user_subscription_channel_missing_slack_webhook_url", 400, "WebhookUrl is missing");

    public static final ErrorCode<SlackUserSubscriptionChannelValidationRestException> MALFORMED_WEBHOOK_URL =
        new ErrorCode<>("user_subscription_channel_malformed_webhook_url", 400, "Malformed webhook url");

    public SlackUserSubscriptionChannelValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
