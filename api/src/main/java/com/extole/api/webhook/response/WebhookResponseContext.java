package com.extole.api.webhook.response;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.event.webhook.WebhookDispatchResultEvent;
import com.extole.api.webhook.Webhook;

public interface WebhookResponseContext extends GlobalContext, LoggerContext {

    WebhookDispatchResultEvent getWebhookDispatchResultEvent();

    Webhook getWebhook();

    @Nullable
    Object getVariable(String variableName);

    @Nullable
    Object getVariable(String name, String key);

    @Nullable
    Object getVariable(String name, String... keys);
}
