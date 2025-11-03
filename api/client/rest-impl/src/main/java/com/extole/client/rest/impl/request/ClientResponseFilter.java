package com.extole.client.rest.impl.request;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.net.InternetDomainName;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.client.rest.client.AccessTokenResponse;
import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.ExtoleCookieType;

@Provider
public class ClientResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext.getEntity() instanceof AccessTokenResponse) {
            AccessTokenResponse accessTokenResponse = (AccessTokenResponse) responseContext.getEntity();
            String host = ((ContainerRequest) requestContext).getBaseUri().getHost();
            if (InternetDomainName.isValid(host)) {
                InternetDomainName domainName = InternetDomainName.from(host);
                // reverting cookie added by ENG-25993 to avoid issues with cookie domains
                // conflicting in browser needed until 2026-07-02
                if (domainName.isUnderPublicSuffix()
                    && "extole.com".equals(domainName.topPrivateDomain().toString())) {
                    new ExtoleCookie(ExtoleCookieType.ADMIN_TOKEN.getCookieName(), "", "/",
                        "extole.com", null, ExtoleCookie.DEFAULT_AGE)
                            .addCookieToResponse(responseContext);
                }
                new ExtoleCookie(ExtoleCookieType.ADMIN_TOKEN.getCookieName(), accessTokenResponse.getToken(), "/",
                    host, null, ExtoleCookie.DEFAULT_AGE)
                        .addCookieToResponse(responseContext);
            } else {
                LOG.error("Unable to set access_token cookie, invalid access_token {} userId {} clientId {}",
                    accessTokenResponse.getToken(), accessTokenResponse.getIdentityId(),
                    accessTokenResponse.getClientId());
            }
        }
    }
}
