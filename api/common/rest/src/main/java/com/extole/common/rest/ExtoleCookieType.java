package com.extole.common.rest;

public enum ExtoleCookieType {
    ACCESS_TOKEN("extole_token"),
    @Deprecated // TODO Remove after 2026-09 once access_token cookie is not supported - ENG-23277
    DEPRECATED_ACCESS_TOKEN("access_token"),
    ADMIN_TOKEN("admin_token"),
    CONTAINER("container"),
    BROWSER_ID("xtl_bid");

    private String cookieName;

    ExtoleCookieType(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    @Override
    public String toString() {
        return getCookieName();
    }
}
