package com.extole.api.webhook;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.client.security.key.ClientKeyApiException;

public interface WebhookRuntimeContext extends GlobalContext, LoggerContext {

    Webhook getWebhook();

    WebhookRequestBuilder createRequestBuilder();

    WebhookRequestBuilder createRequestBuilderWithDefaults() throws ClientKeyApiException;

    @Deprecated // TODO Use void log(String message) instead ENG-16894
    void addLogMessage(String logMessage);

    @Nullable
    Object getVariable(String name);

    @Nullable
    Object getVariable(String name, String key);

    @Nullable
    Object getVariable(String name, String... keys);

    int getAttemptCount();
}
