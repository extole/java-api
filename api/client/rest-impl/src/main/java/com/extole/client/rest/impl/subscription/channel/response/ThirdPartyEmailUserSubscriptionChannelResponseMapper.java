package com.extole.client.rest.impl.subscription.channel.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.response.ThirdPartyEmailSubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.ThirdPartyEmailUserSubscriptionChannel;

@Component
public class ThirdPartyEmailUserSubscriptionChannelResponseMapper
    implements UserSubscriptionChannelResponseMapper<ThirdPartyEmailUserSubscriptionChannel,
        ThirdPartyEmailSubscriptionChannelResponse> {
    @Override
    public ThirdPartyEmailSubscriptionChannelResponse toResponse(ThirdPartyEmailUserSubscriptionChannel channel) {
        return new ThirdPartyEmailSubscriptionChannelResponse(channel.getId().getValue(), channel.getRecipient());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.THIRD_PARTY_EMAIL;
    }
}
