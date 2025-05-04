package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.extole.client.rest.subcription.channel.ChannelType;

public class EmailSubscriptionChannelRequest extends SubscriptionChannelRequest {

    static final String TYPE = "EMAIL";

    @JsonCreator
    public EmailSubscriptionChannelRequest() {
        super(ChannelType.EMAIL);
    }
}
