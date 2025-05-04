package com.extole.client.rest.impl.subscription.channel.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.response.WebhookSubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.WebhookUserSubscriptionChannel;

@Component
public class WebhookUserSubscriptionChannelResponseMapper implements
    UserSubscriptionChannelResponseMapper<WebhookUserSubscriptionChannel, WebhookSubscriptionChannelResponse> {
    @Override
    public WebhookSubscriptionChannelResponse toResponse(WebhookUserSubscriptionChannel channel) {
        return new WebhookSubscriptionChannelResponse(channel.getId().getValue(),
            channel.getWebhookId().getValue());
    }

    @Override
    public ChannelType getType() {
        return ChannelType.WEBHOOK;
    }
}
