package com.extole.webhook.dispatcher.rest.dispatch.request;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class RewardWebhookEventRequest extends WebhookEventRequest {

    static final String TYPE = "REWARD";

    public RewardWebhookEventRequest(
        @JsonProperty(EVENT_ID) Optional<String> eventId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(WEBHOOK_ID) String webhookId,
        @JsonProperty(EVENT) Map<String, Object> event) {
        super(eventId, clientId, webhookId, event);
    }

    @JsonProperty(FIELD_TYPE)
    @Override
    public WebhookDispatchType getType() {
        return WebhookDispatchType.REWARD;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends WebhookEventRequest.Builder<RewardWebhookEventRequest, Builder> {

        private Builder() {
        }

        @Override
        public RewardWebhookEventRequest build() {
            return new RewardWebhookEventRequest(eventId, clientId, webhookId, event);
        }

    }

}
