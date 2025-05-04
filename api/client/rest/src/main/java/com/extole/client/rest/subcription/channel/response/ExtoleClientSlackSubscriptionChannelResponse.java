package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.subcription.channel.ChannelType;

public class ExtoleClientSlackSubscriptionChannelResponse extends SubscriptionChannelResponse {
    static final String TYPE = "EXTOLE_CLIENT_SLACK";

    @JsonCreator
    public ExtoleClientSlackSubscriptionChannelResponse(
        @JsonProperty(JSON_ID) String id) {
        super(id, ChannelType.EXTOLE_CLIENT_SLACK);
    }
}
