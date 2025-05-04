package com.extole.api.event.webhook;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.Sandbox;

@Schema
public interface ConsumerWebhookEvent extends WebhookEvent {

    String getClientDomain();

    String getClientDomainId();

    String getCampaignId();

    String getProgramLabel();

    String getDeviceProfileId();

    @Nullable
    String getIdentityProfileId();

    Sandbox getSandbox();
}
