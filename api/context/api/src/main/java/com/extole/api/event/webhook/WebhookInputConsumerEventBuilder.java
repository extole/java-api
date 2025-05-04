package com.extole.api.event.webhook;

public interface WebhookInputConsumerEventBuilder {

    WebhookInputConsumerEventBuilder withEventName(String name);

    WebhookInputConsumerEventBuilder addData(String name, Object value);

    void send();

}
