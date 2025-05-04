package com.extole.client.rest.impl.subscription.channel.request;

import com.extole.client.rest.subcription.channel.ChannelType;
import com.extole.client.rest.subcription.channel.UserSubscriptionChannelValidationRestException;
import com.extole.client.rest.subcription.channel.request.SubscriptionChannelRequest;
import com.extole.model.service.subscription.UserSubscriptionBuilder;

public interface UserSubscriptionChannelRequestMapper<T extends SubscriptionChannelRequest> {

    void update(UserSubscriptionBuilder builder, T request) throws UserSubscriptionChannelValidationRestException;

    ChannelType getType();
}
