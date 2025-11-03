package com.extole.client.rest.impl.component.sharing.grant;

import static com.extole.common.rest.exception.FatalRestRuntimeException.SOFTWARE_ERROR;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.component.sharing.grant.ComponentGrantAllRequest;
import com.extole.client.rest.component.sharing.grant.ComponentGrantEndpoints;
import com.extole.client.rest.component.sharing.grant.ComponentGrantRequest;
import com.extole.client.rest.component.sharing.grant.ComponentGrantResponse;
import com.extole.client.rest.component.sharing.grant.ComponentGrantRestException;
import com.extole.client.rest.component.sharing.grant.ComponentGranterResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.component.sharing.grant.ComponentGrant.SubscriptionMode;
import com.extole.model.entity.component.sharing.grant.UnallowedSubscriptionModeException;
import com.extole.model.service.component.sharing.grant.ComponentGrantBuilder;
import com.extole.model.service.component.sharing.grant.ComponentGrantNotFoundException;
import com.extole.model.service.component.sharing.grant.ComponentGrantService;

@Provider
public class ComponentGrantEndpointsImpl implements ComponentGrantEndpoints {

    private final ComponentGrantService componentGrantService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ComponentGrantRestMapper componentGrantRestMapper;

    @Inject
    public ComponentGrantEndpointsImpl(ComponentGrantService componentGrantService,
        ClientAuthorizationProvider authorizationProvider,
        ComponentGrantRestMapper componentGrantRestMapper) {
        this.componentGrantService = componentGrantService;
        this.authorizationProvider = authorizationProvider;
        this.componentGrantRestMapper = componentGrantRestMapper;
    }

    @Override
    public List<ComponentGrantResponse> list(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return componentGrantService.listGrants(authorization)
                .stream()
                .map(componentGrant -> componentGrantRestMapper.toComponentGrantResponse(componentGrant,
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
    public ComponentGrantResponse grant(String accessToken, ComponentGrantRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentGrantRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            if (request.getClientId() == null) {
                throw RestExceptionBuilder.newBuilder(ComponentGrantRestException.class)
                    .withErrorCode(ComponentGrantRestException.MISSING_CLIENT_ID)
                    .build();
            }
            return componentGrantRestMapper
                .toComponentGrantResponse(componentGrantService.create(authorization)
                    .withTargetClientId(Id.valueOf(request.getClientId()))
                    .save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentGrantResponse grantAll(String accessToken, ComponentGrantAllRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException {
        if (request == null) {
            request = ComponentGrantAllRequest.builder().build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentGrantBuilder componentGrantBuilder = componentGrantService.create(authorization)
                .withTargetClientId(Client.EXTOLE_CLIENT_ID);
            request.getSubscriptionMode().ifPresent(subscriptionMode -> componentGrantBuilder
                .withSubscriptionMode(SubscriptionMode.valueOf(subscriptionMode.name())));
            return componentGrantRestMapper.toComponentGrantResponse(componentGrantBuilder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UnallowedSubscriptionModeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentGrantResponse revoke(String accessToken, String grantId, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentGrantRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return componentGrantRestMapper
                .toComponentGrantResponse(componentGrantService.update(authorization, Id.valueOf(grantId))
                    .withRevoked()
                    .save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentGrantNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentGrantRestException.class)
                .withErrorCode(ComponentGrantRestException.NOT_FOUND)
                .withCause(e)
                .addParameter("grant_id", grantId)
                .build();
        }
    }

    @Override
    public List<ComponentGranterResponse> listGranters(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return componentGrantService.listGranters(authorization)
                .stream()
                .map(componentGrant -> componentGrantRestMapper.toComponentGranterResponse(componentGrant,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

}
