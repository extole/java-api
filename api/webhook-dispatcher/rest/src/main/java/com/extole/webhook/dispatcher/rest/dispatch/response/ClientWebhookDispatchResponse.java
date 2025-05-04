package com.extole.webhook.dispatcher.rest.dispatch.response;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class ClientWebhookDispatchResponse extends WebhookDispatchResponse {

    static final String TYPE = "CLIENT";

    public ClientWebhookDispatchResponse(
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
        return WebhookDispatchType.CLIENT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ClientWebhookDispatchResponse dispatchResponse) {
        return new Builder(dispatchResponse);
    }

    public static final class Builder extends WebhookDispatchResponse.Builder<ClientWebhookDispatchResponse, Builder> {

        private Builder() {
        }

        private Builder(ClientWebhookDispatchResponse dispatchResponse) {
            super(dispatchResponse);
        }

        @Override
        public ClientWebhookDispatchResponse build() {
            return new ClientWebhookDispatchResponse(eventId, clientId, webhookId, eventTime, event);
        }

    }

}
