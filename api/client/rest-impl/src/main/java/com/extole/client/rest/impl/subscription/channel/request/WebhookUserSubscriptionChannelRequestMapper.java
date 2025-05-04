package com.extole.client.rest.impl.subscription.channel.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.request.WebhookSubscriptionChannelRequest;
import com.extole.id.Id;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.service.subscription.UserSubscriptionBuilder;
import com.extole.model.service.subscription.channel.webhook.WebhookUserSubscriptionChannelBuilder;

@Component
public class WebhookUserSubscriptionChannelRequestMapper
    implements UserSubscriptionChannelRequestMapper<WebhookSubscriptionChannelRequest> {

    @Override
    public void update(UserSubscriptionBuilder builder, WebhookSubscriptionChannelRequest request) {
        WebhookUserSubscriptionChannelBuilder webhookChannelBuilder = builder.addChannel(ChannelType.WEBHOOK);
        webhookChannelBuilder.withWebhookId(Id.valueOf(request.getWebhookId()));
    }

    @Override
    public com.extole.client.rest.subcription.channel.ChannelType getType() {
        return com.extole.client.rest.subcription.channel.ChannelType.WEBHOOK;
    }
}
