package com.extole.common.rest.support.authorization.extractor;

import static com.extole.common.rest.support.authorization.IgnoreAccessTokenCookieDynamicFeature.IGNORE_ACCESS_TOKEN_COOKIE_PROPERTY;

import java.util.Map;
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

        boolean preferRootDomain = Boolean.TRUE.equals(requestContext.getProperty(
            RequestContextAttributeName.PREFER_ROOT_DOMAIN_COOKIE.getAttributeName()));

        ExtoleCookieType primaryCookie =
            preferRootDomain ? ExtoleCookieType.DOMAIN_TOKEN : ExtoleCookieType.ACCESS_TOKEN;
        ExtoleCookieType secondaryCookie =
            preferRootDomain ? ExtoleCookieType.ACCESS_TOKEN : ExtoleCookieType.DOMAIN_TOKEN;

        return getCookieValueFromContext(requestContext, primaryCookie)
            .or(() -> getCookieValueFromContext(requestContext, secondaryCookie))
            .or(() -> shouldFallback(requestContext)
                ? getCookieValueFromContext(requestContext, ExtoleCookieType.DEPRECATED_ACCESS_TOKEN)
                : Optional.empty());
    }

    private static boolean shouldFallback(ContainerRequestContext requestContext) {
        return Boolean.TRUE.equals(requestContext
            .getProperty(RequestContextAttributeName.DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED.getAttributeName()));
    }

    private Optional<String> getCookieValueFromContext(ContainerRequestContext requestContext,
        ExtoleCookieType cookieType) {

        Map<String, Cookie> cookies = requestContext.getCookies();
        Cookie cookie = cookies.get(cookieType.getCookieName());

        if (cookie != null) {
            String value = StringUtils.trimToNull(cookie.getValue());
            return Optional.ofNullable(value);
        }

        return Optional.empty();
    }
}
