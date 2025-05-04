package com.extole.api.event.webhook;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.client.ClientEvent;

@Schema
public interface ClientWebhookEvent extends WebhookEvent {

    int getClientVersion();

    ClientEvent getClientEvent();

    String getUserId();
}
