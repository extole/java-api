package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class WebhookSubscriptionChannelResponse extends SubscriptionChannelResponse {
    static final String TYPE = "WEBHOOK";

    private static final String JSON_WEBHOOK_ID = "webhook_id";

    private final String webhookId;

    @JsonCreator
    public WebhookSubscriptionChannelResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_WEBHOOK_ID) String webhookId) {
        super(id, ChannelType.WEBHOOK);
        this.webhookId = webhookId;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public String getWebhookId() {
        return webhookId;
    }

}
