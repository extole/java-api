package com.extole.client.rest.impl.subscription.channel.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.response.SlackSubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.SlackUserSubscriptionChannel;

@Component
public class SlackUserSubscriptionChannelResponseMapper
    implements UserSubscriptionChannelResponseMapper<SlackUserSubscriptionChannel, SlackSubscriptionChannelResponse> {
    @Override
    public SlackSubscriptionChannelResponse toResponse(SlackUserSubscriptionChannel channel) {
        return new SlackSubscriptionChannelResponse(channel.getId().getValue(), channel.getWebhookUrl().toString());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.SLACK;
    }
}
