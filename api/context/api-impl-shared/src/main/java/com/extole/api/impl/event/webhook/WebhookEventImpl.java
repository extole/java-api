package com.extole.api.impl.event.webhook;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.extole.api.event.webhook.WebhookEvent;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public class WebhookEventImpl implements WebhookEvent {

    private final String clientId;
    private final String eventId;
    private final String eventTime;
    private final String webhookId;
    private final String causeEventId;
    private final String rootEventId;
    private final int causeEventSequence;
    private final Map<String, Object> data;

    public WebhookEventImpl(
        Id<ClientHandle> clientId,
        String eventId,
        String eventTime,
        String webhookId,
        String causeEventId,
        String rootEventId,
        int causeEventSequence,
        Map<String, Object> data) {
        this.clientId = clientId.getValue();
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.webhookId = webhookId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.causeEventSequence = causeEventSequence;
        this.data = ImmutableMap.copyOf(data);
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Override
    public String getWebhookId() {
        return webhookId;
    }

    @Override
    public String getCauseEventId() {
        return causeEventId;
    }

    @Override
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public int getCauseEventSequence() {
        return causeEventSequence;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    private WebhookEventImpl(com.extole.event.webhook.WebhookEventBase webhookEvent) {
        this.clientId = webhookEvent.getClientId().getValue();
        this.eventId = webhookEvent.getEventId().getValue();
        this.eventTime = ExtoleDateTimeFormatters.ISO_INSTANT.format(webhookEvent.getEventTime());
        this.webhookId = webhookEvent.getWebhookId().getValue();
        this.causeEventId = webhookEvent.getCauseEventId().getValue();
        this.rootEventId = webhookEvent.getRootEventId().getValue();
        this.causeEventSequence = webhookEvent.getCauseEventSequence();
        this.data = ImmutableMap.copyOf(webhookEvent.getData());
    }

    public static WebhookEventImpl newInstance(com.extole.event.webhook.WebhookEventBase webhookEvent) {
        return new WebhookEventImpl(webhookEvent);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Id<ClientHandle> clientId;
        private String eventId;
        private String eventTime;
        private String webhookId;
        private String causeEventId;
        private String rootEventId;
        private int causeEventSequence;
        private Map<String, Object> data;

        private Builder() {
        }

        public Builder withClientId(Id<ClientHandle> clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withEventTime(String eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder withWebhookId(String webhookId) {
            this.webhookId = webhookId;
            return this;
        }

        public Builder withCauseEventId(String causeEventId) {
            this.causeEventId = causeEventId;
            return this;
        }

        public Builder withRootEventId(String rootEventId) {
            this.rootEventId = rootEventId;
            return this;
        }

        public Builder withCauseEventSequence(int causeEventSequence) {
            this.causeEventSequence = causeEventSequence;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public WebhookEventImpl build() {
            return new WebhookEventImpl(clientId, eventId, eventTime, webhookId, causeEventId, rootEventId,
                causeEventSequence, data);
        }
    }
}
