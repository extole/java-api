package com.extole.consumer.rest.impl.response;

import static com.google.common.net.HttpHeaders.CONTENT_SECURITY_POLICY;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.common.rest.model.RequestContextAttributeName;

@Provider
public class DefaultContentSecurityPolicyHeaderResponseFilter implements ContainerResponseFilter {

    private static final String SHYFT_CLIENT_ID = "1609009171";
    private static final String EXTOLE_DOMAIN = "https://*.extole.com";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (!SHYFT_CLIENT_ID.equals(getClientId(requestContext))) {
            return;
        }
        addHeaderIfAbsent(responseContext, CONTENT_SECURITY_POLICY, "frame-ancestors 'self' " + EXTOLE_DOMAIN);
    }

    private static String getClientId(ContainerRequestContext requestContext) {
        return (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());
    }

    private void addHeaderIfAbsent(ContainerResponseContext responseContext, String headerName, String value) {
        if (Strings.isNullOrEmpty(responseContext.getHeaderString(headerName))) {
            responseContext.getHeaders().add(headerName, value);
        }
    }
}
