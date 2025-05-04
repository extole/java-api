package com.extole.api.webhook;

import java.util.Map;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;

public interface WebhookRuntimeVariableContext extends GlobalContext, LoggerContext {

    Webhook getWebhook();

    Map<String, Object> getData();
}
