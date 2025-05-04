package com.extole.consumer.rest.impl.common;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class Ie8Filter implements ContainerRequestFilter, WriterInterceptor {
    private static final String IE8_REQUEST = "application/x-ms-application";
    private static final String BROWSER_IE8 = "ie8";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String contentType = requestContext.getHeaderString(HttpHeaders.CONTENT_TYPE);

        if (IE8_REQUEST.equals(contentType)) {
            requestContext.setProperty(BROWSER_IE8, "true");
            requestContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.toString());
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        if (context.getProperty(Ie8Filter.BROWSER_IE8) != null) {
            context.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_TYPE);
        }
        context.proceed();
    }
}
