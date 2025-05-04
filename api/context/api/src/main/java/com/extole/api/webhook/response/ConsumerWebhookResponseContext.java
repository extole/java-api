package com.extole.api.webhook.response;

import com.extole.api.PersonContext;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.event.webhook.ConsumerWebhookEvent;
import com.extole.api.event.webhook.WebhookInputConsumerEventBuilder;

public interface ConsumerWebhookResponseContext extends WebhookResponseContext, PersonContext {

    InternalConsumerEventBuilder internalConsumerEventBuilder();

    WebhookInputConsumerEventBuilder inputConsumerEventBuilder();

    ConsumerWebhookEvent getWebhookEvent();

}
