package com.extole.api.webhook.response;

import com.extole.api.event.webhook.ClientWebhookEvent;

public interface ClientWebhookResponseContext extends WebhookResponseContext {

    ClientWebhookEvent getWebhookEvent();

}
