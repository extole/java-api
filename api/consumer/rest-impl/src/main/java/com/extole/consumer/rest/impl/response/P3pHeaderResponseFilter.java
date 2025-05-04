package com.extole.consumer.rest.impl.response;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

@Provider
public class P3pHeaderResponseFilter implements ContainerResponseFilter {

    private static final String P3P_HEADER = "P3P";
    private static final String P3P_DEFAULT = "CP=\"Please see our privacy policy\"";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (Strings.isNullOrEmpty(responseContext.getHeaderString(P3P_HEADER))) {
            responseContext.getHeaders().add(P3P_HEADER, P3P_DEFAULT);
        }
    }
}
