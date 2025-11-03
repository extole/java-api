package com.extole.common.rest.support.authorization.client;

import static com.extole.common.rest.support.authorization.client.ClientAuthorizationFilter.AUTH_FILTER_PRIORITY;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientNotFoundAuthorizationException;
import com.extole.authorization.service.client.ClientAuthorizationService;
import com.extole.common.rest.exception.ExtoleAuthorizationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.support.authorization.BaseAuthorizationFilter;
import com.extole.model.shared.client.ClientCache;

@Provider
@Priority(AUTH_FILTER_PRIORITY)
public class ClientAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAuthorizationFilter.class);

    public static final int AUTH_FILTER_PRIORITY = BaseAuthorizationFilter.AUTH_FILTER_PRIORITY;

    private final BaseAuthorizationFilter baseAuthorizationFilter;
    private final Function<String, Optional<Authorization>> userAuthorizationExtractor;

    @Autowired
    public ClientAuthorizationFilter(ClientAuthorizationService authorizationService, ClientCache clientCache) {
        this.baseAuthorizationFilter = new BaseAuthorizationFilter(clientCache);
        this.userAuthorizationExtractor = accessToken -> {
            try {
                return Optional.of(authorizationService.getClientAuthorization(accessToken));
            } catch (AuthorizationException e) {
                if (e instanceof ClientNotFoundAuthorizationException) {
                    throw RestExceptionBuilder
                        .newBuilder(ExtoleAuthorizationRestException.class)
                        .withErrorCode(ExtoleAuthorizationRestException.PAYMENT_REQUIRED)
                        .withCause(e)
                        .build();
                }
                // Not all endpoints require an authorization
                LOG.trace("Received access token {} that didn't map to an authorization", accessToken);
                return Optional.empty();
            }
        };
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        baseAuthorizationFilter.filter(requestContext, userAuthorizationExtractor);
    }
}
