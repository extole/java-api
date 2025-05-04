package com.extole.api.webhook;

import java.util.Map;

import com.extole.api.event.Sandbox;

public interface ConsumerWebhookRuntimeContext extends WebhookRuntimeContext {

    Map<String, Object> getData();

    Sandbox getSandbox();

}
