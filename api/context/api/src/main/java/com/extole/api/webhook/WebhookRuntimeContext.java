package com.extole.api.webhook;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.RuntimeVariableContext;
import com.extole.api.client.security.key.ClientKeyApiException;

public interface WebhookRuntimeContext extends GlobalContext, LoggerContext, RuntimeVariableContext {

    Webhook getWebhook();

    WebhookRequestBuilder createRequestBuilder();

    WebhookRequestBuilder createRequestBuilderWithDefaults() throws ClientKeyApiException;

    @Deprecated // TODO Use void log(String message) instead ENG-16894
    void addLogMessage(String logMessage);

    int getAttemptCount();
}
