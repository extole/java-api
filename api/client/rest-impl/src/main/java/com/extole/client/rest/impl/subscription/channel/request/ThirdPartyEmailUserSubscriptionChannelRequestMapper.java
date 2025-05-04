package com.extole.client.rest.impl.subscription.channel.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.request.ThirdPartyEmailSubscriptionChannelRequest;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.service.subscription.UserSubscriptionBuilder;
import com.extole.model.service.subscription.channel.email.ThirdPartyEmailUserSubscriptionChannelBuilder;

@Component
public class ThirdPartyEmailUserSubscriptionChannelRequestMapper
    implements UserSubscriptionChannelRequestMapper<ThirdPartyEmailSubscriptionChannelRequest> {

    @Override
    public void update(UserSubscriptionBuilder builder, ThirdPartyEmailSubscriptionChannelRequest request) {
        ThirdPartyEmailUserSubscriptionChannelBuilder channelBuilder =
            builder.addChannel(ChannelType.THIRD_PARTY_EMAIL);
        channelBuilder.withRecipient(request.getRecipient());
    }

    @Override
    public com.extole.client.rest.subcription.channel.ChannelType getType() {
        return com.extole.client.rest.subcription.channel.ChannelType.THIRD_PARTY_EMAIL;
    }
}
