package com.extole.client.rest.subcription.channel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.client.rest.subcription.channel.ChannelType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = SubscriptionChannelRequest.JSON_CHANNEL_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SlackSubscriptionChannelRequest.class, name = SlackSubscriptionChannelRequest.TYPE),
    @JsonSubTypes.Type(value = ExtoleClientSlackSubscriptionChannelRequest.class,
        name = ExtoleClientSlackSubscriptionChannelRequest.TYPE),
    @JsonSubTypes.Type(value = EmailSubscriptionChannelRequest.class, name = EmailSubscriptionChannelRequest.TYPE),
    @JsonSubTypes.Type(value = ThirdPartyEmailSubscriptionChannelRequest.class,
        name = ThirdPartyEmailSubscriptionChannelRequest.TYPE),
    @JsonSubTypes.Type(value = WebhookSubscriptionChannelRequest.class, name = WebhookSubscriptionChannelRequest.TYPE),
})
public abstract class SubscriptionChannelRequest {

    protected static final String JSON_CHANNEL_TYPE = "type";

    private final ChannelType channelType;

    public SubscriptionChannelRequest(@JsonProperty(JSON_CHANNEL_TYPE) ChannelType channelType) {
        this.channelType = channelType;
    }

    @JsonProperty(JSON_CHANNEL_TYPE)
    public ChannelType getType() {
        return channelType;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
