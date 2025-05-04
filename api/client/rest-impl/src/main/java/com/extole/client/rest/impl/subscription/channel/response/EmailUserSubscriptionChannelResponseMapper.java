package com.extole.client.rest.impl.subscription.channel.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.response.EmailSubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.EmailUserSubscriptionChannel;

@Component
public class EmailUserSubscriptionChannelResponseMapper
    implements UserSubscriptionChannelResponseMapper<EmailUserSubscriptionChannel, EmailSubscriptionChannelResponse> {
    @Override
    public EmailSubscriptionChannelResponse toResponse(EmailUserSubscriptionChannel channel) {
        return new EmailSubscriptionChannelResponse(channel.getId().getValue());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.EMAIL;
    }
}
