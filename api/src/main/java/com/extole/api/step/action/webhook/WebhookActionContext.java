package com.extole.api.step.action.webhook;

import com.extole.api.step.action.StepActionContext;
import com.extole.api.webhook.Webhook;

public interface WebhookActionContext extends StepActionContext {

    @Deprecated // TODO Use void log(String message) instead ENG-16894
    void addLogMessage(String logMessage);

    Webhook getWebhook();

}
