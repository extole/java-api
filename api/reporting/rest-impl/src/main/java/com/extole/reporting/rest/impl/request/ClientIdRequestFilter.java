package com.extole.reporting.rest.impl.request;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.authorization.person.PersonAuthorizationFilter;

@Provider
@Priority(PersonAuthorizationFilter.AUTH_FILTER_PRIORITY - 1)
public class ClientIdRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        String clientId = headers.getFirst(ExtoleHeaderType.CLIENT_ID.getHeaderName());
        if (StringUtils.isNotBlank(clientId)) {
            requestContext.setProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName(), clientId);
        }
    }
}
