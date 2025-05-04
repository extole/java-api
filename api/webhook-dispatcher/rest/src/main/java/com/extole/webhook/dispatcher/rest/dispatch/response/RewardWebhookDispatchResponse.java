package com.extole.webhook.dispatcher.rest.dispatch.response;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class RewardWebhookDispatchResponse extends WebhookDispatchResponse {

    static final String TYPE = "REWARD";

    public RewardWebhookDispatchResponse(
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
        return WebhookDispatchType.REWARD;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RewardWebhookDispatchResponse dispatchResponse) {
        return new Builder(dispatchResponse);
    }

    public static final class Builder extends WebhookDispatchResponse.Builder<RewardWebhookDispatchResponse, Builder> {

        private Builder() {
        }

        private Builder(RewardWebhookDispatchResponse dispatchResponse) {
            super(dispatchResponse);
        }

        @Override
        public RewardWebhookDispatchResponse build() {
            return new RewardWebhookDispatchResponse(eventId, clientId, webhookId, eventTime, event);
        }

    }

}
