package com.extole.client.rest.impl.subscription;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.subscription.channel.response.UserSubscriptionChannelResponseMapperRegistry;
import com.extole.client.rest.subcription.SubscriptionEndpoints;
import com.extole.client.rest.subcription.SubscriptionResponse;
import com.extole.client.rest.subcription.SubscriptionUserResponse;
import com.extole.client.rest.subcription.channel.response.SubscriptionChannelResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.subscription.UserSubscription;
import com.extole.model.entity.user.User;
import com.extole.model.service.subscription.UserSubscriptionQueryBuilder;
import com.extole.model.service.subscription.UserSubscriptionService;
import com.extole.model.shared.user.UserCache;

@Provider
public class SubscriptionEndpointsImpl implements SubscriptionEndpoints {
    private static final String COMMA = ",";

    private final UserSubscriptionService userSubscriptionService;
    private final UserCache userCache;
    private final ClientAuthorizationProvider authorizationProvider;
    private final UserSubscriptionChannelResponseMapperRegistry responseMapperRegistry;

    @Inject
    public SubscriptionEndpointsImpl(UserSubscriptionService userSubscriptionService,
        UserCache userCache,
        ClientAuthorizationProvider authorizationProvider,
        UserSubscriptionChannelResponseMapperRegistry responseMapperRegistry) {
        this.userSubscriptionService = userSubscriptionService;
        this.userCache = userCache;
        this.authorizationProvider = authorizationProvider;
        this.responseMapperRegistry = responseMapperRegistry;
    }

    @Override
    public List<SubscriptionResponse> listSubscriptions(String accessToken, String havingAnyTags,
        String havingAllTags) throws UserAuthorizationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            UserSubscriptionQueryBuilder queryBuilder = userSubscriptionService.createQueryBuilder(authorization);

            if (StringUtils.isNotBlank(havingAnyTags)) {
                queryBuilder.withHavingAnyTags(new HashSet<>(Arrays.asList(havingAnyTags.split(COMMA))));
            }
            if (StringUtils.isNotBlank(havingAllTags)) {
                queryBuilder.withHavingAllTags(new HashSet<>(Arrays.asList(havingAllTags.split(COMMA))));
            }
            return queryBuilder.list()
                .stream()
                .map(item -> toUserSubscriptionResponse(authorization, item))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private List<SubscriptionChannelResponse> mapChannels(UserSubscription userSubscription) {
        return userSubscription.getChannels().stream()
            .map(item -> responseMapperRegistry.getMapper(item.getType()).toResponse(item))
            .collect(Collectors.toList());
    }

    private SubscriptionResponse toUserSubscriptionResponse(Authorization authorization,
        UserSubscription userSubscription) {
        return new SubscriptionResponse(userSubscription.getId().getValue(),
            userSubscription.getHavingAllTags(),
            com.extole.client.rest.subcription.FilteringLevel.valueOf(userSubscription.getFilteringLevel().name()),
            userSubscription.getDedupeDuration().toMillis(),
            mapChannels(userSubscription),
            toSubscriptionUser(getUserById(authorization, userSubscription)));
    }

    private User getUserById(Authorization authorization, UserSubscription userSubscription) {
        return userCache.getById(authorization, userSubscription.getUserId())
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .build());
    }

    private SubscriptionUserResponse toSubscriptionUser(User user) {
        return new SubscriptionUserResponse(user.getId().getValue(),
            user.getNormalizedEmail(),
            user.getFirstName().orElse(null),
            user.getLastName().orElse(null));
    }
}
