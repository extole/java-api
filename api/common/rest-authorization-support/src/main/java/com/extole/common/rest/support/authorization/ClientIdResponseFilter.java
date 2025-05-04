package com.extole.common.rest.support.authorization;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.model.RequestContextAttributeName;

@Provider
public class ClientIdResponseFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ClientIdResponseFilter.class);

    @Inject
    public ClientIdResponseFilter() {
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        String clientId = (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());
        if (clientId == null) {
            LOG.debug("Failed to identify client for client request {}", requestContext.getUriInfo().getRequestUri());
        } else {
            responseContext.getHeaders().put(ExtoleHeaderType.CLIENT_ID.getHeaderName(), Arrays.asList(clientId));
        }
    }
}
