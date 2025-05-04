package com.extole.common.rest.support.authorization.person;

import javax.annotation.Nullable;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.id.Id;

@Component
public class PersonAuthorizationProvider {

    private final ServletRequest servletRequest;
    private final PersonAuthorizationService authorizationService;

    @Autowired
    public PersonAuthorizationProvider(PersonAuthorizationService authorizationService,
        HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        this.authorizationService = authorizationService;
    }

    public PersonAuthorization getPersonAuthorization(@Nullable String accessToken)
        throws UserAuthorizationRestException {
        if (accessToken == null) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING)
                .build();
        }

        Authorization authorization =
            (Authorization) servletRequest.getAttribute(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
        if (authorization != null && authorization.getAccessToken().equals(accessToken)) {
            return (PersonAuthorization) authorization;
        }

        String clientId =
            (String) servletRequest.getAttribute(RequestContextAttributeName.CLIENT_ID.getAttributeName());
        if (StringUtils.isBlank(clientId)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }

        try {
            return authorizationService.getAuthorization(accessToken, Id.valueOf(clientId));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }
}
