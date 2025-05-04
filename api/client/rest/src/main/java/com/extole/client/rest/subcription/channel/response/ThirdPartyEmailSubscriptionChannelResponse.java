package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class ThirdPartyEmailSubscriptionChannelResponse extends SubscriptionChannelResponse {
    static final String TYPE = "THIRD_PARTY_EMAIL";

    private static final String JSON_RECIPIENT = "recipient";

    private final String recipient;

    @JsonCreator
    public ThirdPartyEmailSubscriptionChannelResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_RECIPIENT) String recipient) {
        super(id, ChannelType.THIRD_PARTY_EMAIL);
        this.recipient = recipient;
    }

    @JsonProperty(JSON_RECIPIENT)
    public String getRecipient() {
        return recipient;
    }
}
