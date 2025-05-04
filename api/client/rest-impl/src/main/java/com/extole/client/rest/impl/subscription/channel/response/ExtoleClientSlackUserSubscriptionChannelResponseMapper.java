package com.extole.client.rest.impl.subscription.channel.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.response.ExtoleClientSlackSubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.ExtoleClientSlackUserSubscriptionChannel;

@Component
public class ExtoleClientSlackUserSubscriptionChannelResponseMapper
    implements UserSubscriptionChannelResponseMapper<ExtoleClientSlackUserSubscriptionChannel,
        ExtoleClientSlackSubscriptionChannelResponse> {

    @Override
    public ExtoleClientSlackSubscriptionChannelResponse toResponse(ExtoleClientSlackUserSubscriptionChannel channel) {
        return new ExtoleClientSlackSubscriptionChannelResponse(channel.getId().getValue());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.EXTOLE_CLIENT_SLACK;
    }
}
