package com.extole.api.webhook.response;

import com.extole.api.PersonContext;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.event.webhook.ConsumerWebhookEvent;
import com.extole.api.event.webhook.WebhookInputConsumerEventBuilder;
import com.extole.api.person.Person;

public interface ConsumerWebhookResponseContext extends WebhookResponseContext, PersonContext<Person> {

    InternalConsumerEventBuilder internalConsumerEventBuilder();

    WebhookInputConsumerEventBuilder inputConsumerEventBuilder();

    ConsumerWebhookEvent getWebhookEvent();

}
