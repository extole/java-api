package com.extole.client.rest.impl.subscription.channel.request;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.subcription.channel.ChannelType;

@Component
public class UserSubscriptionChannelRequestMapperRegistry {

    private final Map<ChannelType, UserSubscriptionChannelRequestMapper> channelUploaders;

    @Autowired
    public UserSubscriptionChannelRequestMapperRegistry(List<UserSubscriptionChannelRequestMapper> channelUploaders) {
        this.channelUploaders = ImmutableMap.copyOf(channelUploaders.stream()
            .collect(Collectors.toMap(item -> item.getType(), Function.identity())));
    }

    public UserSubscriptionChannelRequestMapper getRequestMapper(ChannelType channelType) {
        UserSubscriptionChannelRequestMapper mapper = channelUploaders.get(channelType);
        if (mapper == null) {
            throw new RuntimeException("Request mapper of type:" + channelType + " not found");
        }
        return mapper;
    }
}
