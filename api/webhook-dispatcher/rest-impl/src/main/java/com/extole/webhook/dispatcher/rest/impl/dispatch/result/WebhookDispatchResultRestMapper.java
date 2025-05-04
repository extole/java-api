package com.extole.webhook.dispatcher.rest.impl.dispatch.result;

import java.time.ZoneId;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.extole.webhook.dispatch.result.event.service.WebhookDispatchResultRecentEvent;
import com.extole.webhook.dispatcher.rest.dispatch.result.WebhookDispatchResultResponse;
import com.extole.webhook.dispatcher.service.WebhookDispatchResult;

@Component
public class WebhookDispatchResultRestMapper {

    public WebhookDispatchResultResponse
        toWebhookDispatchResultResponse(WebhookDispatchResultRecentEvent webhookDispatchResult, ZoneId timeZone) {
        return new WebhookDispatchResultResponse(
            webhookDispatchResult.getEventId().getValue(),
            webhookDispatchResult.getEventTime().atZone(timeZone),
            webhookDispatchResult.getWebhookEventId().getValue(),
            webhookDispatchResult.getCauseEventId().getValue(),
            webhookDispatchResult.getRootEventId().getValue(),
            webhookDispatchResult.getWebhookId().getValue(),
            webhookDispatchResult.getUrl().map(url -> url.toString()),
            webhookDispatchResult.getRequestBody(),
            webhookDispatchResult.getRequestHeaders(),
            webhookDispatchResult.getResponseStatusCode(),
            webhookDispatchResult.getResponseBody(),
            webhookDispatchResult.getResponseHeaders(),
            webhookDispatchResult.getAttemptCount(),
            webhookDispatchResult.getMethod(),
            webhookDispatchResult.getLogMessages(),
            webhookDispatchResult.getTags(),
            Collections.emptyMap());
    }

    public WebhookDispatchResultResponse toWebhookDispatchResultResponse(WebhookDispatchResult webhookDispatchResult,
        ZoneId timeZone) {
        return new WebhookDispatchResultResponse(
            webhookDispatchResult.getEventId().getValue(),
            webhookDispatchResult.getEventTime().atZone(timeZone),
            webhookDispatchResult.getWebhookEventId().getValue(),
            webhookDispatchResult.getCauseEventId().getValue(),
            webhookDispatchResult.getRootEventId().getValue(),
            webhookDispatchResult.getWebhookId().getValue(),
            webhookDispatchResult.getUrl().map(url -> url.toString()),
            webhookDispatchResult.getRequestBody(),
            webhookDispatchResult.getRequestHeaders(),
            webhookDispatchResult.getResponseStatusCode(),
            webhookDispatchResult.getResponseBody(),
            webhookDispatchResult.getResponseHeaders(),
            webhookDispatchResult.getAttemptCount(),
            webhookDispatchResult.getMethod(),
            webhookDispatchResult.getLogMessages(),
            webhookDispatchResult.getTags(),
            webhookDispatchResult.getResponse());
    }

}
