package com.extole.common.rest.support.authorization.person;

import static com.extole.common.rest.support.authorization.person.PersonAuthorizationFilter.AUTH_FILTER_PRIORITY;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.BaseAuthorizationFilter;
import com.extole.id.Id;
import com.extole.model.shared.client.ClientCache;

@Provider
@Priority(AUTH_FILTER_PRIORITY)
public class PersonAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PersonAuthorizationFilter.class);

    public static final int AUTH_FILTER_PRIORITY = BaseAuthorizationFilter.AUTH_FILTER_PRIORITY + 1;

    private final BaseAuthorizationFilter baseAuthorizationFilter;
    private final PersonAuthorizationService authorizationService;

    @Autowired
    public PersonAuthorizationFilter(PersonAuthorizationService authorizationService, ClientCache clientCache) {
        this.authorizationService = authorizationService;
        this.baseAuthorizationFilter = new BaseAuthorizationFilter(clientCache);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Function<String, Optional<Authorization>> authorizationExtractor =
            createPersonAuthorizationExtractor(requestContext);
        baseAuthorizationFilter.filter(requestContext, authorizationExtractor);
    }

    private Function<String, Optional<Authorization>>
        createPersonAuthorizationExtractor(ContainerRequestContext requestContext) {
        return accessToken -> {
            try {
                String clientId =
                    (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());
                if (StringUtils.isBlank(clientId)) {
                    return Optional.empty();
                }
                return Optional.of(authorizationService.getAuthorization(accessToken, Id.valueOf(clientId)));
            } catch (AuthorizationException e) {
                // Not all endpoints require an authorization
                LOG.trace("Received access token {} that didn't map to an authorization", accessToken);
                return Optional.empty();
            }
        };
    }
}
