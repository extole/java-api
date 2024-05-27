package com.extole.api.webhook.response;

import com.extole.api.PersonContext;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.event.webhook.ConsumerWebhookEvent;

public interface ConsumerWebhookResponseContext extends WebhookResponseContext, PersonContext {

    InternalConsumerEventBuilder internalConsumerEventBuilder();

    ConsumerWebhookEvent getWebhookEvent();

}
