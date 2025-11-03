package com.extole.client.rest.impl.subscription.channel.response;

import com.extole.client.rest.subcription.channel.response.SubscriptionChannelResponse;
import com.extole.model.entity.subscription.ChannelType;
import com.extole.model.entity.subscription.UserSubscriptionChannel;

public interface UserSubscriptionChannelResponseMapper<R extends UserSubscriptionChannel, C extends SubscriptionChannelResponse> {

    C toResponse(R channel);

    ChannelType getType();
}
