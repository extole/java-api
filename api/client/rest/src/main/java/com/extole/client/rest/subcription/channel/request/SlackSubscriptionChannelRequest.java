package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class SlackSubscriptionChannelRequest extends SubscriptionChannelRequest {

    static final String TYPE = "SLACK";

    private static final String JSON_WEBHOOK_URL = "webhook_url";

    private final String webhookUrl;

    @JsonCreator
    public SlackSubscriptionChannelRequest(
        @JsonProperty(JSON_WEBHOOK_URL) String webhookUrl) {
        super(ChannelType.SLACK);
        this.webhookUrl = webhookUrl;
    }

    @JsonProperty(JSON_WEBHOOK_URL)
    public String getWebhookUrl() {
        return webhookUrl;
    }
}
