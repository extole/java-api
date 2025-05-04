package com.extole.common.rest.support.authorization;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.extractor.AccessTokenExtractor;
import com.extole.common.rest.support.authorization.extractor.AuthorizationHeaderAccessTokenExtractor;
import com.extole.common.rest.support.authorization.extractor.CookieAccessTokenExtractor;
import com.extole.common.rest.support.authorization.extractor.QueryParameterAccessTokenExtractor;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

public class BaseAuthorizationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAuthorizationFilter.class);

    public static final int AUTH_FILTER_PRIORITY = Priorities.AUTHORIZATION;

    private final ClientCache clientCache;
    private final List<AccessTokenExtractor> accessTokenExtractors;

    public BaseAuthorizationFilter(ClientCache clientCache) {
        this.clientCache = clientCache;
        this.accessTokenExtractors = Arrays.asList(
            new QueryParameterAccessTokenExtractor(),
            new AuthorizationHeaderAccessTokenExtractor(),
            new CookieAccessTokenExtractor());
    }

    public void filter(ContainerRequestContext requestContext,
        Function<String, Optional<Authorization>> authorizationExtractor) {
        Optional<String> accessToken = accessTokenExtractors
            .stream()
            .map(extractor -> extractor.extract(requestContext))
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());

        if (accessToken.isPresent()) {
            requestContext.setProperty(RequestContextAttributeName.ACCESS_TOKEN.getAttributeName(), accessToken.get());
            authorizationExtractor.apply(accessToken.get())
                .ifPresent(authorization -> handleAuthorization(requestContext, authorization));
        }
    }

    private void handleAuthorization(ContainerRequestContext requestContext, Authorization authorization) {
        requestContext.setProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName(), authorization);
        requestContext.setProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName(),
            authorization.getClientId().getValue());
        try {
            String clientShortName = clientCache.getById(authorization.getClientId()).getShortName();
            requestContext.setProperty(RequestContextAttributeName.CLIENT_SHORT_NAME.getAttributeName(),
                clientShortName);
        } catch (ClientNotFoundException e) {
            LOG.error("Retrieved authorization {} for non existent client", authorization);
        }
    }
}
