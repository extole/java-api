package com.extole.webhook.dispatcher.rest.dispatch.request;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = WebhookEventRequest.FIELD_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConsumerWebhookEventRequest.class, name = ConsumerWebhookEventRequest.TYPE),
    @JsonSubTypes.Type(value = ClientWebhookEventRequest.class, name = ClientWebhookEventRequest.TYPE),
    @JsonSubTypes.Type(value = RewardWebhookEventRequest.class, name = RewardWebhookEventRequest.TYPE),
    @JsonSubTypes.Type(value = PartnerWebhookEventRequest.class, name = PartnerWebhookEventRequest.TYPE),
})
public abstract class WebhookEventRequest {

    protected static final String FIELD_TYPE = "type";
    protected static final String EVENT_ID = "event_id";
    protected static final String CLIENT_ID = "client_id";
    protected static final String WEBHOOK_ID = "webhook_id";
    protected static final String EVENT = "event";

    private final Optional<String> eventId;
    private final String clientId;
    private final String webhookId;
    private final Map<String, Object> event;

    public WebhookEventRequest(
        Optional<String> eventId,
        String clientId,
        String webhookId,
        Map<String, Object> event) {
        this.eventId = eventId;
        this.clientId = clientId;
        this.webhookId = webhookId;
        this.event = event != null ? ImmutableMap.copyOf(event) : ImmutableMap.of();
    }

    public abstract WebhookDispatchType getType();

    @JsonProperty(EVENT_ID)
    public Optional<String> getEventId() {
        return eventId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(WEBHOOK_ID)
    public String getWebhookId() {
        return webhookId;
    }

    @JsonProperty(EVENT)
    public Map<String, Object> getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder<
        REQUEST extends WebhookEventRequest,
        BUILDER extends Builder<REQUEST, BUILDER>> {

        protected Optional<String> eventId = Optional.empty();
        protected String clientId;
        protected String webhookId;
        protected Map<String, Object> event = ImmutableMap.of();

        protected Builder() {
        }

        public BUILDER withEventId(String eventId) {
            this.eventId = Optional.of(eventId);
            return (BUILDER) this;
        }

        public BUILDER withClientId(String clientId) {
            this.clientId = clientId;
            return (BUILDER) this;
        }

        public BUILDER withWebhookId(String webhookId) {
            this.webhookId = webhookId;
            return (BUILDER) this;
        }

        public BUILDER withEvent(Map<String, Object> event) {
            this.event = ImmutableMap.copyOf(event);
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }

}
