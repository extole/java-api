package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.extole.client.rest.subcription.channel.ChannelType;

public class ExtoleClientSlackSubscriptionChannelRequest extends SubscriptionChannelRequest {

    static final String TYPE = "EXTOLE_CLIENT_SLACK";

    @JsonCreator
    public ExtoleClientSlackSubscriptionChannelRequest() {
        super(ChannelType.EXTOLE_CLIENT_SLACK);
    }
}
