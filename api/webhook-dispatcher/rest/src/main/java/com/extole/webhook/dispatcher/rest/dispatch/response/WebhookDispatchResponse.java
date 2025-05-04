package com.extole.webhook.dispatcher.rest.dispatch.response;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;
import com.extole.webhook.dispatcher.rest.dispatch.WebhookDispatchType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = WebhookDispatchResponse.FIELD_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConsumerWebhookDispatchResponse.class, name = ConsumerWebhookDispatchResponse.TYPE),
    @JsonSubTypes.Type(value = ClientWebhookDispatchResponse.class, name = ClientWebhookDispatchResponse.TYPE),
    @JsonSubTypes.Type(value = RewardWebhookDispatchResponse.class, name = RewardWebhookDispatchResponse.TYPE),
    @JsonSubTypes.Type(value = PartnerWebhookDispatchResponse.class, name = PartnerWebhookDispatchResponse.TYPE),
})
public abstract class WebhookDispatchResponse {

    protected static final String FIELD_TYPE = "type";
    protected static final String EVENT_ID = "event_id";
    protected static final String CLIENT_ID = "client_id";
    protected static final String WEBHOOK_ID = "webhook_id";
    protected static final String EVENT_TIME = "event_time";
    protected static final String EVENT = "event";

    private final String eventId;
    private final String clientId;
    private final String webhookId;
    private final ZonedDateTime eventTime;
    private final Map<String, Object> event;

    public WebhookDispatchResponse(
        String eventId,
        String clientId,
        String webhookId,
        ZonedDateTime eventTime,
        Map<String, Object> event) {
        this.eventId = eventId;
        this.clientId = clientId;
        this.webhookId = webhookId;
        this.eventTime = eventTime;
        this.event = event != null ? ImmutableMap.copyOf(event) : ImmutableMap.of();
    }

    public abstract WebhookDispatchType getType();

    @JsonProperty(EVENT_ID)
    public String getEventId() {
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

    @JsonProperty(EVENT_TIME)
    public ZonedDateTime getEventTime() {
        return eventTime;
    }

    @JsonProperty(EVENT)
    public Map<String, Object> getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder<RESPONSE extends WebhookDispatchResponse,
        BUILDER extends Builder<RESPONSE, BUILDER>> {

        protected String eventId;
        protected String clientId;
        protected String webhookId;
        protected ZonedDateTime eventTime;
        protected Map<String, Object> event = ImmutableMap.of();

        protected Builder() {
        }

        public Builder(WebhookDispatchResponse dispatchResponse) {
            this.eventId = dispatchResponse.getEventId();
            this.clientId = dispatchResponse.getClientId();
            this.webhookId = dispatchResponse.getWebhookId();
            this.eventTime = dispatchResponse.getEventTime();
            this.event = dispatchResponse.getEvent();
        }

        public BUILDER withEventId(String eventId) {
            this.eventId = eventId;
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

        public BUILDER withEventTime(ZonedDateTime eventTime) {
            this.eventTime = eventTime;
            return (BUILDER) this;
        }

        public BUILDER withEvent(Map<String, Object> event) {
            this.event = ImmutableMap.copyOf(event);
            return (BUILDER) this;
        }

        public abstract RESPONSE build();

    }

}
