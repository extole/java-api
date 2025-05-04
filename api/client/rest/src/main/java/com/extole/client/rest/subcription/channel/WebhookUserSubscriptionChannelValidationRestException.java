package com.extole.client.rest.subcription.channel;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class WebhookUserSubscriptionChannelValidationRestException
    extends UserSubscriptionChannelValidationRestException {

    public static final ErrorCode<WebhookUserSubscriptionChannelValidationRestException> WEBHOOK_NOT_FOUND =
        new ErrorCode<>("webhook_not_found", 400, "Webhook not found", "webhook_id");

    public static final ErrorCode<WebhookUserSubscriptionChannelValidationRestException> INVALID_WEBHOOK_TYPE =
        new ErrorCode<>("invalid_webhook_type", 400, "Webhook type is invalid", "webhook_id", "webhook_type");

    public WebhookUserSubscriptionChannelValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
