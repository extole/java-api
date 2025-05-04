package com.extole.consumer.rest.impl.response;

import static javax.ws.rs.core.HttpHeaders.SET_COOKIE;

import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.ExtoleCookieType;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.consumer.rest.impl.request.ConsumerContextAttributeName;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;
import com.extole.consumer.service.client.ClientConsentService;
import com.extole.consumer.service.client.CookieDuration;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@Provider
@DropsAccessTokenCookie
public class CookieResponseFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(CookieResponseFilter.class);
    private static final int DEFAULT_AGE = ExtoleCookie.DEFAULT_AGE;
    private static final int SESSION_AGE = ExtoleCookie.SESSION_AGE;
    public static final int ON_BROWSER_CLOSE_REMOVE_COOKIE_MAX_AGE = -1;

    private final ClientConsentService clientConsentService;
    private final ClientCache clientCache;

    @Autowired
    public CookieResponseFilter(ClientConsentService clientConsentService, ClientCache clientCache) {
        this.clientConsentService = clientConsentService;
        this.clientCache = clientCache;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        PublicProgram program = getProgram(requestContext);
        Optional<PersonAuthorization> authorization = getAuthorization(requestContext);
        Id<ClientHandle> clientId = program.getClientId();
        try {
            if (authorization.isPresent()) {
                PersonAuthorization personAuthorization = authorization.get();
                injectTokenHeader(responseContext, personAuthorization.getAccessToken());
                if (!clientConsentService.isCookieEnabled(personAuthorization, personAuthorization.getIdentity())) {
                    responseContext.getHeaders().put(SET_COOKIE, Collections.emptyList());
                    return;
                }
                int maxAge = calculateMaxAge(getCookieDuration(personAuthorization));
                String value = personAuthorization.getAccessToken();
                new ExtoleCookie(ExtoleCookieType.ACCESS_TOKEN.getCookieName(), value, "/",
                    program.getProgramDomain().toString(), null, maxAge)
                        .addCookieToResponse(responseContext);
            } else {
                injectTokenHeader(responseContext, "");
                new ExtoleCookie(ExtoleCookieType.ACCESS_TOKEN.getCookieName(), "", "/",
                    program.getProgramDomain().toString(), null, ON_BROWSER_CLOSE_REMOVE_COOKIE_MAX_AGE)
                        .addCookieToResponse(responseContext);
                boolean deprecatedAccessTokenCookieAllowed = isDeprecatedAccessTokenCookieAllowed(clientId);
                if (deprecatedAccessTokenCookieAllowed) {
                    new ExtoleCookie(ExtoleCookieType.DEPRECATED_ACCESS_TOKEN.getCookieName(), "", "/",
                        program.getProgramDomain().toString(), null, ON_BROWSER_CLOSE_REMOVE_COOKIE_MAX_AGE)
                            .addCookieToResponse(responseContext);
                }
            }
            // can't respond with a 500 to a zone request
        } catch (Exception e) {
            if (authorization.isPresent()) {
                LOG.error("Unable to set access_token cookie, invalid access_token for client "
                    + clientId + " person " + authorization.get().getIdentityId(), e);
            } else {
                LOG.error("Unable to set access_token cookie, invalid access_token for client "
                    + clientId, e);
            }
            // avoid printing stack traces on errors
        }
    }

    private static Optional<PersonAuthorization> getAuthorization(ContainerRequestContext requestContext) {
        Object authorization = requestContext.getProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
        return Optional.ofNullable((PersonAuthorization) authorization);
    }

    private static PublicProgram getProgram(ContainerRequestContext requestContext) {
        return (PublicProgram) getProperty(requestContext, ConsumerContextAttributeName.PROGRAM);
    }

    private static Object getProperty(ContainerRequestContext requestContext, ConsumerContextAttributeName attribute) {
        return requestContext.getProperty(attribute.getAttributeName());
    }

    private static void injectTokenHeader(ContainerResponseContext responseContext, String value) {
        responseContext.getHeaders().add(ExtoleHeaderType.TOKEN.getHeaderName(), value);
    }

    private CookieDuration getCookieDuration(PersonAuthorization authorization) throws AuthorizationException {
        return clientConsentService.getThirdPartyCookieDuration(authorization, authorization.getIdentity());
    }

    private boolean isDeprecatedAccessTokenCookieAllowed(Id<ClientHandle> clientId) throws ClientNotFoundException {
        return clientCache.getById(clientId).getCoreSettings().isDeprecatedAccessTokenCookieAllowed();
    }

    private int calculateMaxAge(CookieDuration cookieDuration) {
        int maxAge;
        switch (cookieDuration) {
            case YEAR:
                maxAge = DEFAULT_AGE;
                break;
            case SESSION:
            case NO_COOKIE:
                maxAge = SESSION_AGE;
                break;
            default:
                maxAge = DEFAULT_AGE;
        }
        return maxAge;
    }
}
