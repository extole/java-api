package com.extole.reporting.rest.impl.report.execution;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.authorization.person.PersonAuthorizationProvider;

@Component
public class CombinedAuthorizationProvider {

    private final HttpServletRequest servletRequest;
    private final ClientAuthorizationProvider clientAuthorizationProvider;
    private final PersonAuthorizationProvider personAuthorizationProvider;

    @Autowired
    CombinedAuthorizationProvider(HttpServletRequest servletRequest,
        ClientAuthorizationProvider clientAuthorizationProvider,
        PersonAuthorizationProvider personAuthorizationProvider) {
        this.servletRequest = servletRequest;
        this.clientAuthorizationProvider = clientAuthorizationProvider;
        this.personAuthorizationProvider = personAuthorizationProvider;
    }

    public Authorization getAuthorization(@Nullable String accessToken) throws UserAuthorizationRestException {
        Authorization authorization =
            (Authorization) servletRequest
                .getAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName());

        if (authorization != null && authorization.getAccessToken().equals(accessToken)) {
            return authorization;
        }
        if (accessToken == null) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING).build();
        }

        try {
            return clientAuthorizationProvider.getClientAuthorization(accessToken);
        } catch (UserAuthorizationRestException e) {
            return personAuthorizationProvider.getPersonAuthorization(accessToken);
        }
    }
}
