package com.extole.client.rest.subcription.channel.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.client.rest.subcription.channel.ChannelType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = SubscriptionChannelResponse.JSON_TYPE,
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SlackSubscriptionChannelResponse.class, name = SlackSubscriptionChannelResponse.TYPE),
    @JsonSubTypes.Type(value = ExtoleClientSlackSubscriptionChannelResponse.class,
        name = ExtoleClientSlackSubscriptionChannelResponse.TYPE),
    @JsonSubTypes.Type(value = EmailSubscriptionChannelResponse.class, name = EmailSubscriptionChannelResponse.TYPE),
    @JsonSubTypes.Type(value = WebhookSubscriptionChannelResponse.class,
        name = WebhookSubscriptionChannelResponse.TYPE),
    @JsonSubTypes.Type(value = ThirdPartyEmailSubscriptionChannelResponse.class,
        name = ThirdPartyEmailSubscriptionChannelResponse.TYPE),
})
public abstract class SubscriptionChannelResponse {

    protected static final String JSON_ID = "id";
    protected static final String JSON_TYPE = "type";

    private final String id;
    private final ChannelType channelType;

    public SubscriptionChannelResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) ChannelType channelType) {
        this.id = id;
        this.channelType = channelType;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_TYPE)
    public ChannelType getType() {
        return channelType;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
