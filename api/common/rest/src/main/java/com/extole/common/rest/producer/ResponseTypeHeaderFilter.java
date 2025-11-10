package com.extole.common.rest.producer;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class ResponseTypeHeaderFilter implements ContainerRequestFilter {
    private static final String CSV_EXTENSION = ".csv";
    private static final String PSV_EXTENSION = ".psv";
    private static final String CSV_MIME_TYPE = "text/csv";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.endsWith(CSV_EXTENSION) || path.endsWith(PSV_EXTENSION)) {
            requestContext.getHeaders().putSingle("Accept", CSV_MIME_TYPE);
        }
    }
}
