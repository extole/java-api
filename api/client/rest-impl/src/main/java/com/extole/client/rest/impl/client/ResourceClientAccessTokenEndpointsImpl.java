package com.extole.client.rest.impl.client;

import java.time.Instant;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.resource.ResourceAuthorizationNotFoundException;
import com.extole.authorization.service.client.resource.ResourceAuthorizationService;
import com.extole.authorization.service.resource.ResourceAuthorization;
import com.extole.client.rest.client.AccessTokenResourceResponse;
import com.extole.client.rest.client.AccessTokenResourceType;
import com.extole.client.rest.client.ResourceAccessTokenEndpoints;
import com.extole.client.rest.client.ResourceAccessTokenResponse;
import com.extole.client.rest.client.ResourceAccessTokenRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;

@Provider
public class ResourceClientAccessTokenEndpointsImpl implements ResourceAccessTokenEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    public ResourceClientAccessTokenEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ResourceAuthorizationService appAuthorizationService) {
        this.authorizationProvider = authorizationProvider;
        this.resourceAuthorizationService = appAuthorizationService;
    }

    @Override
    public ResourceAccessTokenResponse getToken(String accessToken)
        throws UserAuthorizationRestException {
        try {
            return mapToResponse(resourceAuthorizationService.getByAccessToken(accessToken));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withCause(e)
                .build();
        }
    }

    @Override
    public void delete(String accessToken, String accessTokenToDelete)
        throws UserAuthorizationRestException, ResourceAccessTokenRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            resourceAuthorizationService.invalidate(authorization, accessTokenToDelete);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ResourceAuthorizationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ResourceAccessTokenRestException.class)
                .withErrorCode(ResourceAccessTokenRestException.NO_SUCH_RESOURCE_TOKEN)
                .withCause(e)
                .build();
        }
    }

    private ResourceAccessTokenResponse mapToResponse(ResourceAuthorization resourceAuthorization) {
        return ResourceAccessTokenResponse.builder()
            .withToken(resourceAuthorization.getAccessToken())
            .withIdentityId(resourceAuthorization.getIdentityId())
            .withExpiresIn(resourceAuthorization.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond())
            .withResources(resourceAuthorization.getResources()
                .stream()
                .map(resource -> new AccessTokenResourceResponse(resource.getId(),
                    AccessTokenResourceType.valueOf(resource.getType().name())))
                .collect(Collectors.toList()))
            .build();
    }
}
