package com.extole.common.rest.model;

public enum RequestContextAttributeName {
    ACCESS_TOKEN("access_token"),
    AUTHORIZATION("authorization"),
    CLIENT_ID("client_id"),
    CLIENT_SHORT_NAME("client_short_name"),
    @Deprecated // TODO Remove after 2026-09 once access_token cookie is not supported - ENG-23277
    DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED("deprecated_access_token_cookie_allowed"),
    REQUEST_TIME_ZONE("request_time_zone");

    private final String attributeName;

    RequestContextAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }
}
