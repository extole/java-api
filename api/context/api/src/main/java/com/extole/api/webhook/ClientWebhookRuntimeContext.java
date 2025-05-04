package com.extole.api.webhook;

import com.extole.api.event.client.ClientEvent;

public interface ClientWebhookRuntimeContext extends WebhookRuntimeContext {

    ClientEvent getClientEvent();
}
