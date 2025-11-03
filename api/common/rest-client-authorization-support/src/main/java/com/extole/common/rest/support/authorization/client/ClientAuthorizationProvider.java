package com.extole.common.rest.support.authorization.client;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientNotFoundAuthorizationException;
import com.extole.authorization.service.Identity;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.client.ClientAuthorizationService;
import com.extole.authorization.service.client.user.UserAuthorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RequestContextAttributeName;

@Component
public class ClientAuthorizationProvider {

    private final HttpServletRequest servletRequest;
    private final ClientAuthorizationService authorizationService;

    @Autowired
    public ClientAuthorizationProvider(ClientAuthorizationService authorizationService,
        HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        this.authorizationService = authorizationService;
    }

    public ClientAuthorization getClientAuthorization(@Nullable String accessToken)
        throws UserAuthorizationRestException {
        if (accessToken == null) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING)
                .build();
        }

        Authorization authorization = (Authorization) servletRequest
            .getAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
        if (authorization != null && authorization.getAccessToken().equals(accessToken)) {
            return (ClientAuthorization) authorization;
        }

        try {
            return authorizationService.getClientAuthorization(accessToken);
        } catch (AuthorizationException e) {
            if (e instanceof ClientNotFoundAuthorizationException) {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.PAYMENT_REQUIRED)
                    .withCause(e)
                    .build();
            }
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    public UserAuthorization getUserAuthorization(@Nullable String accessToken) throws UserAuthorizationRestException {
        ClientAuthorization authorization = getClientAuthorization(accessToken);
        if (authorization.getIdentity().getType() == Identity.Type.USER) {
            return (UserAuthorization) authorization;
        }
        throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
            .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
            .build();
    }
}
