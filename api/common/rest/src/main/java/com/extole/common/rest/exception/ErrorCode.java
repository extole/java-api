package com.extole.common.rest.exception;

import java.util.HashSet;
import java.util.Set;

import com.extole.common.lang.ToString;

public class ErrorCode<T> {
    private final String name;
    private final int httpCode;
    private final Set<String> attributes = new HashSet<>();
    private final String message;

    public ErrorCode(String name, int httpCode, String message, String... attributes) {
        this.name = name;
        this.httpCode = httpCode;
        this.message = message;
        for (String attribute : attributes) {
            this.attributes.add(attribute);
        }
    }

    public String getName() {
        return name;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getMessage() {
        return message;
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
