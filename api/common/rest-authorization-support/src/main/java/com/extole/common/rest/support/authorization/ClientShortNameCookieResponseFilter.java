package com.extole.common.rest.support.authorization;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.ExtoleCookieType;
import com.extole.common.rest.model.RequestContextAttributeName;

@Provider
public class ClientShortNameCookieResponseFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ClientShortNameCookieResponseFilter.class);
    private static final int ONE_HOUR_IN_SECONDS = 60 * 60;

    @Inject
    public ClientShortNameCookieResponseFilter() {
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String clientShortName =
            (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_SHORT_NAME.getAttributeName());
        if (StringUtils.isBlank(clientShortName)) {
            LOG.debug("Failed to identify client short name for request {}",
                requestContext.getUriInfo().getRequestUri());
        } else {
            new ExtoleCookie(ExtoleCookieType.CLIENT_SHORT_NAME.getCookieName(), clientShortName, "/", null, null,
                ONE_HOUR_IN_SECONDS).addCookieToResponse(responseContext);
        }
    }
}
