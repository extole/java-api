package com.extole.api.impl.event.webhook;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.api.event.Sandbox;
import com.extole.api.event.webhook.ConsumerWebhookEvent;
import com.extole.api.impl.event.SandboxImpl;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;
import com.extole.id.Id;

public class ConsumerWebhookEventImpl implements ConsumerWebhookEvent {

    private final String eventId;
    private final String clientId;
    private final String eventTime;
    private final String webhookId;
    private final String causeEventId;
    private final String rootEventId;
    private final String clientDomain;
    private final String clientDomainId;
    private final int causeEventSequence;
    private final String campaignId;
    private final String programLabel;
    private final String deviceProfileId;
    private final String identityProfileId;
    private final Sandbox sandbox;
    private final Map<String, Object> data;

    public ConsumerWebhookEventImpl(
        Id<ClientHandle> clientId,
        String eventId,
        String eventTime,
        String webhookId,
        String causeEventId,
        String rootEventId,
        String clientDomain,
        String clientDomainId,
        int causeEventSequence,
        String campaignId,
        String programLabel,
        String deviceProfileId,
        String identityProfileId,
        String sandbox,
        String container,
        Map<String, Object> data) {
        this.eventId = eventId;
        this.clientId = clientId.getValue();
        this.eventTime = eventTime;
        this.webhookId = webhookId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.clientDomain = clientDomain;
        this.clientDomainId = clientDomainId;
        this.causeEventSequence = causeEventSequence;
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.deviceProfileId = deviceProfileId;
        this.identityProfileId = identityProfileId;
        this.sandbox = new SandboxImpl(sandbox, container);
        this.data = ImmutableMap.copyOf(data);
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getClientId() {
        return clientId;
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
    public String getClientDomain() {
        return clientDomain;
    }

    @Override
    public String getClientDomainId() {
        return clientDomainId;
    }

    @Override
    public int getCauseEventSequence() {
        return causeEventSequence;
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getDeviceProfileId() {
        return deviceProfileId;
    }

    @Nullable
    @Override
    public String getIdentityProfileId() {
        return identityProfileId;
    }

    @Override
    public Sandbox getSandbox() {
        return sandbox;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    private ConsumerWebhookEventImpl(com.extole.event.webhook.ConsumerWebhookEvent webhookEvent) {
        this.clientId = webhookEvent.getClientId().getValue();
        this.eventId = webhookEvent.getEventId().getValue();
        this.eventTime = ExtoleDateTimeFormatters.ISO_INSTANT.format(webhookEvent.getEventTime());
        this.webhookId = webhookEvent.getWebhookId().getValue();
        this.causeEventId = webhookEvent.getCauseEventId().getValue();
        this.rootEventId = webhookEvent.getRootEventId().getValue();
        this.clientDomain = webhookEvent.getClientDomain();
        this.clientDomainId = webhookEvent.getClientDomainId().getValue();
        this.causeEventSequence = webhookEvent.getCauseEventSequence();
        this.campaignId = webhookEvent.getCampaignId().getValue();
        this.programLabel = webhookEvent.getProgramLabel();
        this.deviceProfileId = webhookEvent.getDeviceProfileId().getValue();
        this.identityProfileId = webhookEvent.getIdentityProfileId().map(Id::getValue).orElse(null);
        this.sandbox = new SandboxImpl(webhookEvent.getSandbox(), webhookEvent.getContainer());
        this.data = ImmutableMap.copyOf(webhookEvent.getData());
    }

    public static ConsumerWebhookEventImpl newInstance(com.extole.event.webhook.ConsumerWebhookEvent webhookEvent) {
        return new ConsumerWebhookEventImpl(webhookEvent);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String eventId;
        private String eventTime;
        private String webhookId;
        private String causeEventId;
        private String rootEventId;
        private String clientDomain;
        private String clientDomainId;
        private int causeEventSequence;
        private String campaignId;
        private String programLabel;
        private String deviceProfileId;
        private String identityProfileId;
        private String sandbox;
        private String container;
        private Id<ClientHandle> clientId;
        private Map<String, Object> data;

        private Builder() {
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

        public Builder withClientDomain(String clientDomain) {
            this.clientDomain = clientDomain;
            return this;
        }

        public Builder withClientDomainId(String clientDomainId) {
            this.clientDomainId = clientDomainId;
            return this;
        }

        public Builder withCauseEventSequence(int causeEventSequence) {
            this.causeEventSequence = causeEventSequence;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = programLabel;
            return this;
        }

        public Builder withDeviceProfileId(String deviceProfileId) {
            this.deviceProfileId = deviceProfileId;
            return this;
        }

        public Builder withIdentityProfileId(String identityProfileId) {
            this.identityProfileId = identityProfileId;
            return this;
        }

        public Builder withSandbox(String sandbox) {
            this.sandbox = sandbox;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withClientId(Id<ClientHandle> clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = Maps.newHashMap(data);
            return this;
        }

        public ConsumerWebhookEventImpl build() {
            return new ConsumerWebhookEventImpl(clientId, eventId, eventTime, webhookId, causeEventId, rootEventId,
                clientDomain, clientDomainId, causeEventSequence, campaignId, programLabel, deviceProfileId,
                identityProfileId, sandbox, container, data);
        }
    }
}
