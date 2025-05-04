package com.extole.client.rest.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AssociatedWebhookUserSubscriptionChannelResponse {

    private static final String JSON_SUBSCRIPTION_ID = "subscription_id";
    private static final String JSON_CHANNEL_ID = "channel_id";

    private final String subscriptionId;
    private final String channelId;

    @JsonCreator
    public AssociatedWebhookUserSubscriptionChannelResponse(
        @JsonProperty(JSON_SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(JSON_CHANNEL_ID) String channelId) {
        this.subscriptionId = subscriptionId;
        this.channelId = channelId;
    }

    @JsonProperty(JSON_SUBSCRIPTION_ID)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty(JSON_CHANNEL_ID)
    public String getChannelId() {
        return channelId;
    }
}
