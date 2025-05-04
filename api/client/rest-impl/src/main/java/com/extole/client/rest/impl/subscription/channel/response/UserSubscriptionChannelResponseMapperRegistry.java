package com.extole.client.rest.impl.subscription.channel.response;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.subscription.ChannelType;

@Component
public class UserSubscriptionChannelResponseMapperRegistry {

    private final Map<ChannelType, UserSubscriptionChannelResponseMapper> channelMappers;

    @Autowired
    public UserSubscriptionChannelResponseMapperRegistry(List<UserSubscriptionChannelResponseMapper> channelMappers) {
        this.channelMappers = ImmutableMap.copyOf(channelMappers.stream()
            .collect(Collectors.toMap(item -> item.getType(), Function.identity())));
    }

    public UserSubscriptionChannelResponseMapper getMapper(ChannelType channelType) {
        UserSubscriptionChannelResponseMapper mapper = channelMappers.get(channelType);
        if (mapper == null) {
            throw new RuntimeException("Response mapper for type=" + channelType + " not found");
        }
        return mapper;
    }
}
