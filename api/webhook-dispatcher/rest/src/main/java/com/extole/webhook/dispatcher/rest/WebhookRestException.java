package com.extole.webhook.dispatcher.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class WebhookRestException extends ExtoleRestException {

    public static final ErrorCode<WebhookRestException> WEBHOOK_NOT_FOUND = new ErrorCode<>(
        "webhook_not_found", 400, "Webhook is not found", "webhook_id");

    public WebhookRestException(String uniqueId, ErrorCode<WebhookRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
