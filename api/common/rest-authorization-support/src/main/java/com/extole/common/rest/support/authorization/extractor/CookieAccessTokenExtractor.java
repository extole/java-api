package com.extole.common.rest.support.authorization.extractor;

import static com.extole.common.rest.support.authorization.IgnoreAccessTokenCookieDynamicFeature.IGNORE_ACCESS_TOKEN_COOKIE_PROPERTY;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;

import org.apache.commons.lang3.StringUtils;

import com.extole.common.rest.ExtoleCookieType;
import com.extole.common.rest.model.RequestContextAttributeName;

public final class CookieAccessTokenExtractor implements AccessTokenExtractor {

    @Override
    public Optional<String> extract(ContainerRequestContext requestContext) {
        if (Boolean.TRUE.equals(requestContext.getProperty(IGNORE_ACCESS_TOKEN_COOKIE_PROPERTY))) {
            return Optional.empty();
        }

        // TODO as part of ENG-21238 change to have only one access token cookie per auth type - person, user
        Optional<String> accessToken = getCookieValueFromContext(requestContext, ExtoleCookieType.ADMIN_TOKEN);
        if (accessToken.isPresent()) {
            return accessToken;
        }
        accessToken = getCookieValueFromContext(requestContext, ExtoleCookieType.ACCESS_TOKEN);

        if (accessToken.isEmpty() && shouldFallback(requestContext)) {
            accessToken = getCookieValueFromContext(requestContext, ExtoleCookieType.DEPRECATED_ACCESS_TOKEN);
        }

        return accessToken;
    }

    private static boolean shouldFallback(ContainerRequestContext requestContext) {
        return Boolean.TRUE.equals(requestContext
            .getProperty(RequestContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName()));
    }

    private Optional<String> getCookieValueFromContext(ContainerRequestContext requestContext,
        ExtoleCookieType cookieType) {
        Cookie cookie = requestContext.getCookies().get(cookieType.getCookieName());
        if (cookie != null) {
            String value = StringUtils.trimToNull(cookie.getValue());
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }
}
