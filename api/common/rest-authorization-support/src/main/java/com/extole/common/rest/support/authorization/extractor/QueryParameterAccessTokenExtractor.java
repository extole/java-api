package com.extole.common.rest.support.authorization.extractor;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.lang3.StringUtils;

public final class QueryParameterAccessTokenExtractor implements AccessTokenExtractor {

    private static final String ACCESS_TOKEN_QUERY_STRING = "access_token";

    @Override
    public Optional<String> extract(ContainerRequestContext requestContext) {
        String token = requestContext.getUriInfo().getQueryParameters().getFirst(ACCESS_TOKEN_QUERY_STRING);
        return Optional.ofNullable(StringUtils.defaultIfBlank(token, null));
    }

}
