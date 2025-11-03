package com.extole.client.rest.impl.component.sharing.subscription;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriberResponse;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriptionCreateRequest;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriptionEndpoints;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriptionResponse;
import com.extole.client.rest.component.sharing.subscription.ComponentSubscriptionRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.component.sharing.subscription.ComponentSubscriptionNotFoundException;
import com.extole.model.service.component.sharing.subscription.ComponentSubscriptionService;
import com.extole.model.service.component.sharing.subscription.MissingRequiredGrantException;

@Provider
public class ComponentSubscriptionEndpointsImpl implements ComponentSubscriptionEndpoints {

    private final ComponentSubscriptionService componentSubscriptionService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ComponentSubscriptionRestMapper componentSubscriptionRestMapper;

    @Inject
    public ComponentSubscriptionEndpointsImpl(
        ComponentSubscriptionService componentSubscriptionService,
        ClientAuthorizationProvider authorizationProvider,
        ComponentSubscriptionRestMapper componentSubscriptionRestMapper) {
        this.componentSubscriptionService = componentSubscriptionService;
        this.authorizationProvider = authorizationProvider;
        this.componentSubscriptionRestMapper = componentSubscriptionRestMapper;
    }

    @Override
    public List<ComponentSubscriptionResponse> list(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return componentSubscriptionService.listSubscriptions(authorization)
            .stream()
            .map(subscription -> componentSubscriptionRestMapper.toComponentSubscriptionResponse(
                subscription,
                timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public List<ComponentSubscriberResponse> listSubscribers(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return componentSubscriptionService.listSubscribers(authorization)
                .stream()
                .map(subscription -> componentSubscriptionRestMapper.toComponentSubscriberResponse(
                    subscription,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentSubscriptionResponse subscribe(String accessToken, ComponentSubscriptionCreateRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, ComponentSubscriptionRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (request.getClientId() == null) {
            throw RestExceptionBuilder.newBuilder(ComponentSubscriptionRestException.class)
                .withErrorCode(ComponentSubscriptionRestException.MISSING_CLIENT_ID)
                .build();
        }
        try {
            return componentSubscriptionRestMapper
                .toComponentSubscriptionResponse(componentSubscriptionService.createBuilder(authorization)
                    .withTargetClientId(Id.valueOf(request.getClientId()))
                    .save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (MissingRequiredGrantException e) {
            throw RestExceptionBuilder.newBuilder(ComponentSubscriptionRestException.class)
                .withErrorCode(ComponentSubscriptionRestException.GRANT_REQUIRED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentSubscriptionResponse unsubscribe(String accessToken, String subscriptionId, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentSubscriptionRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return componentSubscriptionRestMapper.toComponentSubscriptionResponse(
                componentSubscriptionService.update(authorization, Id.valueOf(subscriptionId))
                    .withUnsubscribed()
                    .save(),
                timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentSubscriptionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentSubscriptionRestException.class)
                .withErrorCode(ComponentSubscriptionRestException.NOT_FOUND)
                .withCause(e)
                .addParameter("subscription_id", subscriptionId)
                .build();
        } catch (MissingRequiredGrantException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
