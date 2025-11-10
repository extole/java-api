package com.extole.common.rest;

public enum SameSiteCookieValue {
    NONE("None"),
    LAX("Lax"),
    STRICT("Strict");

    private final String value;

    SameSiteCookieValue(String value) {
        this.value = value;
    }

    String getValue() {
        return value;
    }
}
