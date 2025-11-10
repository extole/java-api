package com.extole.common.rest.support.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

class ServletDetails {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ACCESS_TOKEN_PARAMETER_NAME = "access_token";
    private static final String UNIDENTIFIED = "unidentified";
    private static final String EMPTY = "";
    private final Map<String, List<String>> requestHeaders;
    private final Map<String, List<String>> responseHeaders;
    private final List<String> accessTokenQueryParameters;
    private final String accessTokenFromCookie;
    private final String requestUri;
    private final String method;
    private final Object responseEntity;
    private final int responseStatus;
    private final Object accessTokenProperty;
    private final String authorizationHeader;
    private final Optional<String> accessToken;

    ServletDetails(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        requestUri = getRequestUri(requestContext);
        requestHeaders = getRequestHeaders(requestContext);
        accessTokenQueryParameters = getAccessTokenQueryParameters(requestContext);
        accessTokenFromCookie = getAccessTokenFromCookie(requestContext);
        authorizationHeader = getAuthorizationHeader(requestContext);
        responseHeaders = getResponseHeaders(responseContext);

        responseEntity = responseContext.getEntity();
        responseStatus = responseContext.getStatus();
        accessTokenProperty = requestContext.getProperty(ACCESS_TOKEN_PARAMETER_NAME);
        method = requestContext.getMethod();

        accessToken = parseAccessToken();
    }

    private String getRequestUri(ContainerRequestContext requestContext) {
        if (requestContext.getUriInfo() != null && requestContext.getUriInfo().getRequestUri() != null) {
            return requestContext.getUriInfo().getRequestUri().toString();
        }
        return UNIDENTIFIED;
    }

    private Map<String, List<String>> getRequestHeaders(ContainerRequestContext requestContext) {
        if (requestContext.getHeaders() != null) {
            return deepCopy(requestContext.getHeaders());
        }
        return Collections.emptyMap();
    }

    private Map<String, List<String>> getResponseHeaders(ContainerResponseContext responseContext) {
        if (responseContext.getHeaders() != null) {
            return deepCopy(responseContext.getStringHeaders());
        }
        return Collections.emptyMap();
    }

    private List<String> getAccessTokenQueryParameters(ContainerRequestContext requestContext) {
        if (requestContext.getUriInfo() != null && requestContext.getUriInfo().getQueryParameters() != null &&
            requestContext.getUriInfo().getQueryParameters().get(ACCESS_TOKEN_PARAMETER_NAME) != null) {
            return ImmutableList
                .copyOf(requestContext.getUriInfo().getQueryParameters().get(ACCESS_TOKEN_PARAMETER_NAME));
        }
        return Collections.emptyList();
    }

    private String getAccessTokenFromCookie(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(ACCESS_TOKEN_PARAMETER_NAME);
        if (cookie != null) {
            String value = cookie.getValue();
            if (!Strings.isNullOrEmpty(value)) {
                return value;
            }
        }
        return EMPTY;
    }

    private String getAuthorizationHeader(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString(AUTHORIZATION_HEADER);
    }

    private Optional<String> parseAccessToken() {
        String token = null;

        if (accessTokenProperty instanceof String) {
            token = (String) accessTokenProperty;
        }

        if (token == null && !accessTokenQueryParameters.isEmpty()) {
            token = accessTokenQueryParameters.get(0);
        }

        if (token == null && !accessTokenFromCookie.isEmpty()) {
            token = accessTokenFromCookie;
        }

        if (token == null) {
            if (authorizationHeader != null) {
                String authorization = authorizationHeader.trim();
                String value = authorization.substring(authorization.lastIndexOf(" ") + 1);
                if (!Strings.isNullOrEmpty(value)) {
                    token = value;
                }
            }
        }
        return Optional.ofNullable(token);
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, List<String>> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public Object getResponseEntity() {
        return responseEntity;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public Optional<String> getAccessToken() {
        return accessToken;
    }

    private static ImmutableMap<String, List<String>> deepCopy(Map<String, List<String>> map) {
        Builder<String, List<String>> builder = ImmutableMap.builder();
        map.entrySet().forEach(entrySet -> {
            builder.put(entrySet.getKey(), ImmutableList.copyOf(entrySet.getValue()));
        });
        return builder.build();
    }
}
