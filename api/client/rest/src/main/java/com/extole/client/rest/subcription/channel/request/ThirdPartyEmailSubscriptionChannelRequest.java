package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class ThirdPartyEmailSubscriptionChannelRequest extends SubscriptionChannelRequest {

    static final String TYPE = "THIRD_PARTY_EMAIL";
    private static final String JSON_RECIPIENT = "recipient";

    private final String recipient;

    @JsonCreator
    public ThirdPartyEmailSubscriptionChannelRequest(
        @JsonProperty(JSON_RECIPIENT) String recipient) {
        super(ChannelType.THIRD_PARTY_EMAIL);
        this.recipient = recipient;
    }

    @JsonProperty(JSON_RECIPIENT)
    public String getRecipient() {
        return recipient;
    }

}
