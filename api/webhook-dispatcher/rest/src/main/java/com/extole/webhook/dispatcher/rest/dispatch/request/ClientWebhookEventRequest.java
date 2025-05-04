package com.extole.webhook.dispatcher.rest.dispatch.request;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class ClientWebhookEventRequest extends WebhookEventRequest {

    static final String TYPE = "CLIENT";

    public ClientWebhookEventRequest(
        @JsonProperty(EVENT_ID) Optional<String> eventId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(WEBHOOK_ID) String webhookId,
        @JsonProperty(EVENT) Map<String, Object> event) {
        super(eventId, clientId, webhookId, event);
    }

    @JsonProperty(FIELD_TYPE)
    @Override
    public WebhookDispatchType getType() {
        return WebhookDispatchType.CLIENT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends WebhookEventRequest.Builder<ClientWebhookEventRequest, Builder> {

        private Builder() {
        }

        @Override
        public ClientWebhookEventRequest build() {
            return new ClientWebhookEventRequest(eventId, clientId, webhookId, event);
        }

    }

}
