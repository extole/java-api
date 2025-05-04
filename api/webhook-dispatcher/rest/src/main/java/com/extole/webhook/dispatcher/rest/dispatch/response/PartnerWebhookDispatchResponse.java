package com.extole.webhook.dispatcher.rest.dispatch.response;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

public final class PartnerWebhookDispatchResponse extends WebhookDispatchResponse {

    static final String TYPE = "PARTNER";

    public PartnerWebhookDispatchResponse(
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
        return WebhookDispatchType.PARTNER;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(PartnerWebhookDispatchResponse dispatchResponse) {
        return new Builder(dispatchResponse);
    }

    public static final class Builder extends WebhookDispatchResponse.Builder<PartnerWebhookDispatchResponse, Builder> {

        private Builder() {
        }

        private Builder(PartnerWebhookDispatchResponse dispatchResponse) {
            super(dispatchResponse);
        }

        @Override
        public PartnerWebhookDispatchResponse build() {
            return new PartnerWebhookDispatchResponse(eventId, clientId, webhookId, eventTime, event);
        }

    }

}
