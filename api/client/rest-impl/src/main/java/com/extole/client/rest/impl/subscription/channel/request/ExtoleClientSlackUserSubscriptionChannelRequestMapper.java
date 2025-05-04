package com.extole.client.rest.impl.subscription.channel.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.request.ExtoleClientSlackSubscriptionChannelRequest;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.service.subscription.UserSubscriptionBuilder;

@Component
public class ExtoleClientSlackUserSubscriptionChannelRequestMapper
    implements UserSubscriptionChannelRequestMapper<ExtoleClientSlackSubscriptionChannelRequest> {

    @Override
    public void update(UserSubscriptionBuilder builder, ExtoleClientSlackSubscriptionChannelRequest request) {
        builder.addChannel(ChannelType.EXTOLE_CLIENT_SLACK);
    }

    @Override
    public com.extole.client.rest.subcription.channel.ChannelType getType() {
        return com.extole.client.rest.subcription.channel.ChannelType.EXTOLE_CLIENT_SLACK;
    }
}
