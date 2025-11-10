package com.extole.common.rest;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.ResponseBuilder;

public class ExtoleCookie {
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final String SET_COOKIE = HttpHeaders.SET_COOKIE;
    public static final int DEFAULT_AGE = 365 * 24 * 60 * 60;
    public static final int SESSION_AGE = -1;
    private final NewCookie cookieHolder;

    public ExtoleCookie(String name,
        String value,
        String path,
        String domain,
        String comment,
        int maxAge) {
        NewCookie newCookie = new NewCookie(name, value, path, domain, 1, comment, maxAge,
            new Date(System.currentTimeMillis() + maxAge * MILLISECONDS_PER_SECOND), true, true);
        this.cookieHolder = new NewCookieSameSiteAdapter(newCookie, SameSiteCookieValue.NONE);
    }

    public ExtoleCookie(NewCookie cookie) {
        this.cookieHolder = new NewCookieSameSiteAdapter(cookie, SameSiteCookieValue.NONE);
    }

    public void addCookieToResponse(ContainerResponseContext response) {
        response.getHeaders().add(SET_COOKIE, this.cookieHolder.toString());
    }

    public void addCookieToResponse(HttpServletResponse response) {
        response.addHeader(SET_COOKIE, this.cookieHolder.toString());
    }

    public void addCookieToResponse(ResponseBuilder response) {
        response.cookie(cookieHolder);
    }

    public String getCookieValue() {
        return this.cookieHolder.toString();
    }

    private static class NewCookieSameSiteAdapter extends NewCookie {
        private final NewCookie newCookie;
        private final SameSiteCookieValue sameSiteCookieValue;

        NewCookieSameSiteAdapter(NewCookie newCookie, SameSiteCookieValue sameSiteCookieValue) {
            super(newCookie);
            this.newCookie = newCookie;
            this.sameSiteCookieValue = sameSiteCookieValue;
        }

        private String sameSiteSecureString() {
            return ";SameSite=" + sameSiteCookieValue.getValue();
        }

        @Override
        public String toString() {
            return newCookie.toString() + sameSiteSecureString();
        }
    }
}
