package com.extole.api.event.webhook;

import java.util.Map;

public interface WebhookEvent {

    String getEventId();

    String getClientId();

    String getEventTime();

    String getWebhookId();

    String getCauseEventId();

    String getRootEventId();

    int getCauseEventSequence();

    Map<String, Object> getData();
}
