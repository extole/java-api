package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class WebhookSubscriptionChannelRequest extends SubscriptionChannelRequest {
    static final String TYPE = "WEBHOOK";

    static final String JSON_WEBHOOK_ID = "webhook_id";

    private final String webhookId;

    @JsonCreator
    public WebhookSubscriptionChannelRequest(@JsonProperty(JSON_WEBHOOK_ID) String webhookId) {
        super(ChannelType.WEBHOOK);
        this.webhookId = webhookId;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public String getWebhookId() {
        return webhookId;
    }

}
