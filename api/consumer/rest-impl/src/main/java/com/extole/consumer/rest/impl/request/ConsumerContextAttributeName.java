package com.extole.consumer.rest.impl.request;

public enum ConsumerContextAttributeName {
    BROWSER_ID("browser_id"),
    PROGRAM("program"),
    @Deprecated // TODO Remove after 2026-09 once access_token cookie is not supported - ENG-23277
    DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED("deprecated_access_token_cookie_allowed");

    private final String attributeName;

    ConsumerContextAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }
}
