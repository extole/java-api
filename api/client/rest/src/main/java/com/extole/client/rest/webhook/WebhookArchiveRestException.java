package com.extole.client.rest.webhook;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class WebhookArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<WebhookArchiveRestException> WEBHOOK_ASSOCIATED_WITH_WEBHOOK_CONTROLLER_ACTION =
        new ErrorCode<>(
            "webhook_associated_with_webhook_controller_action", 400,
            "Can't archive or disable a webhook associated with webhook controller actions", "webhook_id",
            "webhook_controller_actions");

    public static final ErrorCode<
        WebhookArchiveRestException> WEBHOOK_ASSOCIATED_WITH_WEBHOOK_USER_SUBSCRIPTION_CHANNEL =
            new ErrorCode<>(
                "webhook_associated_with_webhook_user_subscription_channel", 400,
                "Can't archive or disable a webhook associated with webhook user subscription channels",
                "webhook_id",
                "webhook_user_subscription_channels");

    public WebhookArchiveRestException(String uniqueId, ErrorCode<WebhookArchiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
