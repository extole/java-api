package com.extole.webhook.dispatcher.rest.dispatch;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class WebhookDispatchRestException extends ExtoleRestException {

    public static final ErrorCode<WebhookDispatchRestException> MISSING_WEBHOOK_ID = new ErrorCode<>(
        "missing_webhook_id", 400, "Webhook id is missing in the request");

    public static final ErrorCode<WebhookDispatchRestException> MISSING_CLIENT_ID = new ErrorCode<>(
        "missing_client_id", 400, "Client id is missing in the request");

    public static final ErrorCode<WebhookDispatchRestException> REQUEST_CLIENT_ID_INVALID = new ErrorCode<>(
        "request_client_id_invalid", 400, "The request and authorization must be for the same client");

    public static final ErrorCode<WebhookDispatchRestException> WEBHOOK_DISPATCH_FAILED = new ErrorCode<>(
        "webhook_dispatch_failed", 400, "Webhook dispatch failed");

    public WebhookDispatchRestException(String uniqueId, ErrorCode<WebhookDispatchRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
