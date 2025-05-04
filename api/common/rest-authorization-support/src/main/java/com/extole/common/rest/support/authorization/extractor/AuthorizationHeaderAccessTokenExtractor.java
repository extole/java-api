package com.extole.common.rest.support.authorization.extractor;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;

public final class AuthorizationHeaderAccessTokenExtractor implements AccessTokenExtractor {

    @Override
    public Optional<String> extract(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        return removeAuthenticationMethod(authorizationHeader);
    }

    private Optional<String> removeAuthenticationMethod(String header) {
        if (StringUtils.isBlank(header)) {
            return Optional.empty();
        }
        String trimmedHeader = header.trim();
        return Optional.ofNullable(
            StringUtils.defaultIfBlank(trimmedHeader.substring(trimmedHeader.lastIndexOf(" ") + 1), null));
    }

}
