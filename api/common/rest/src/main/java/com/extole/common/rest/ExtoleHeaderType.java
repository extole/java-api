package com.extole.common.rest;

public enum ExtoleHeaderType {
    SOURCE_IP("X-Forwarded-For"),
    ERROR_MESSAGE("X-Extole-Error"),
    CLIENT_ID("X-Extole-Client-Id"),
    TOKEN("X-Extole-Token"),
    TIME_ZONE("Time-Zone"),
    COOKIE_CONSENT("X-Extole-Cookie-Consent");

    private final String headerName;

    ExtoleHeaderType(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public String toString() {
        return getHeaderName();
    }
}
