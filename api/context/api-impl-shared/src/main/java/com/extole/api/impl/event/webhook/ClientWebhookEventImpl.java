package com.extole.api.impl.event.webhook;

import java.util.Map;
import java.util.stream.Collectors;

import com.extole.api.event.client.ClientEvent;
import com.extole.api.event.webhook.ClientWebhookEvent;
import com.extole.api.impl.event.client.ClientEventImpl;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public class ClientWebhookEventImpl implements ClientWebhookEvent {

    private final String clientId;
    private final String eventId;
    private final String eventTime;
    private final String webhookId;
    private final String causeEventId;
    private final String rootEventId;
    private final int causeEventSequence;
    private final int clientVersion;
    private final ClientEvent clientEvent;
    private final String userId;
    private final Map<String, Object> data;

    public ClientWebhookEventImpl(
        Id<ClientHandle> clientId,
        String eventId,
        String eventTime,
        String webhookId,
        String causeEventId,
        String rootEventId,
        int causeEventSequence,
        int clientVersion,
        ClientEvent clientEvent,
        Id<?> userId) {
        this.clientId = clientId.getValue();
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.webhookId = webhookId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.causeEventSequence = causeEventSequence;
        this.clientVersion = clientVersion;
        this.clientEvent = clientEvent;
        this.userId = userId.getValue();
        this.data = clientEvent.getData().entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
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
    public int getClientVersion() {
        return clientVersion;
    }

    @Override
    public ClientEvent getClientEvent() {
        return clientEvent;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Deprecated // TODO refactor api webhook events so that ClientWebhookEvent does not have getData() in ENG-21251
    @Override
    public Map<String, Object> getData() {
        return data;
    }

    private ClientWebhookEventImpl(com.extole.event.webhook.ClientWebhookEvent webhookEvent) {
        this.clientId = webhookEvent.getClientId().getValue();
        this.eventId = webhookEvent.getEventId().getValue();
        this.eventTime = ExtoleDateTimeFormatters.ISO_INSTANT.format(webhookEvent.getEventTime());
        this.webhookId = webhookEvent.getWebhookId().getValue();
        this.causeEventId = webhookEvent.getCauseEventId().getValue();
        this.rootEventId = webhookEvent.getRootEventId().getValue();
        this.causeEventSequence = webhookEvent.getCauseEventSequence();
        this.clientVersion = webhookEvent.getClientVersion();
        this.userId = webhookEvent.getUserId().getValue();
        this.clientEvent = ClientEventImpl.newInstance(webhookEvent.getClientEvent());
        this.data = clientEvent.getData().entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    public static ClientWebhookEventImpl newInstance(com.extole.event.webhook.ClientWebhookEvent webhookEvent) {
        return new ClientWebhookEventImpl(webhookEvent);
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
        private int clientVersion;
        private ClientEvent clientEvent;
        private Id<?> userId;

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

        public Builder withClientVersion(int clientVersion) {
            this.clientVersion = clientVersion;
            return this;
        }

        public Builder withClientEvent(ClientEvent clientEvent) {
            this.clientEvent = clientEvent;
            return this;
        }

        public Builder withUserId(Id<?> userId) {
            this.userId = userId;
            return this;
        }

        public ClientWebhookEventImpl build() {
            return new ClientWebhookEventImpl(clientId, eventId, eventTime, webhookId, causeEventId, rootEventId,
                causeEventSequence, clientVersion, clientEvent, userId);
        }
    }

}
