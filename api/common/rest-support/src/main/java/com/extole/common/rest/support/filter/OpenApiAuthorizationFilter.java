package com.extole.common.rest.support.filter;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.model.RequestContextAttributeName;

@Provider
@Priority(FilterPriority.OPENAPI)
public class OpenApiAuthorizationFilter implements ContainerRequestFilter {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OpenApiAuthorizationFilter.class);
    private static final String OPENAPI_JSON = "openapi.json";
    private static final String OPENAPI_YAML = "openapi.yaml";

    @Override
    public void filter(ContainerRequestContext requestContext) throws java.io.IOException {
        String path = requestContext.getUriInfo().getPath();

        if (path.endsWith(OPENAPI_JSON) || path.endsWith(OPENAPI_YAML)) {
            Authorization authorization = (Authorization) requestContext
                .getProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName());

            if (authorization == null || !authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
                LOG.info("Unauthorized OpenAPI access attempt: {}", path);
                requestContext
                    .abortWith(javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}
