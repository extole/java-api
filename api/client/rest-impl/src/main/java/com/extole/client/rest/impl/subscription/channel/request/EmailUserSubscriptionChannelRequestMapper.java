package com.extole.client.rest.impl.subscription.channel.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.request.EmailSubscriptionChannelRequest;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.service.subscription.UserSubscriptionBuilder;

@Component
public class EmailUserSubscriptionChannelRequestMapper
    implements UserSubscriptionChannelRequestMapper<EmailSubscriptionChannelRequest> {

    @Override
    public void update(UserSubscriptionBuilder builder, EmailSubscriptionChannelRequest request) {
        builder.addChannel(ChannelType.EMAIL);
    }

    @Override
    public com.extole.client.rest.subcription.channel.ChannelType getType() {
        return com.extole.client.rest.subcription.channel.ChannelType.EMAIL;
    }
}
