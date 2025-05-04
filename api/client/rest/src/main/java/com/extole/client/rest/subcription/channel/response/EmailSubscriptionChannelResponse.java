package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class EmailSubscriptionChannelResponse extends SubscriptionChannelResponse {
    static final String TYPE = "EMAIL";

    @JsonCreator
    public EmailSubscriptionChannelResponse(
        @JsonProperty(JSON_ID) String id) {
        super(id, ChannelType.EMAIL);
    }
}
