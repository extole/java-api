package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class SlackSubscriptionChannelResponse extends SubscriptionChannelResponse {
    static final String TYPE = "SLACK";

    private static final String JSON_WEBHOOK_URL = "webhook_url";

    private final String webhookUrl;

    @JsonCreator
    public SlackSubscriptionChannelResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_WEBHOOK_URL) String webhookUrl) {
        super(id, ChannelType.SLACK);
        this.webhookUrl = webhookUrl;
    }

    @JsonProperty(JSON_WEBHOOK_URL)
    public String getWebhookUrl() {
        return webhookUrl;
    }
}
