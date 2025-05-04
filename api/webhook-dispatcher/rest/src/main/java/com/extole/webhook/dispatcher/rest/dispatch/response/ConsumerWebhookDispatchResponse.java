package com.extole.webhook.dispatcher.rest.dispatch.response;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class ConsumerWebhookDispatchResponse extends WebhookDispatchResponse {

    static final String TYPE = "CONSUMER";

    public ConsumerWebhookDispatchResponse(
        @JsonProperty(EVENT_ID) String eventId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(WEBHOOK_ID) String webhookId,
        @JsonProperty(EVENT_TIME) ZonedDateTime eventTime,
        @JsonProperty(EVENT) Map<String, Object> event) {
        super(eventId, clientId, webhookId, eventTime, event);
    }

    @JsonProperty(FIELD_TYPE)
    @Override
    public WebhookDispatchType getType() {
        return WebhookDispatchType.CONSUMER;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ConsumerWebhookDispatchResponse dispatchResponse) {
        return new Builder(dispatchResponse);
    }

    public static final class Builder
        extends WebhookDispatchResponse.Builder<ConsumerWebhookDispatchResponse, Builder> {

        private Builder() {
        }

        private Builder(ConsumerWebhookDispatchResponse dispatchResponse) {
            super(dispatchResponse);
        }

        @Override
        public ConsumerWebhookDispatchResponse build() {
            return new ConsumerWebhookDispatchResponse(eventId, clientId, webhookId, eventTime, event);
        }

    }

}
