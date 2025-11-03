package com.extole.common;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <p>
 * Represents an URL template,
 * which can be an actual URL, or it can contain variables.
 * </p>
 * <p>
 * Variables must follow this pattern:<br>
 * {variable_name}<br>
 * where "variable_name" represents the variable name.
 * </p>
 * <p>
 * Sample URL template with three variables - hotel, booking, userId:<br>
 * <code>https://example.com/hotels/{hotel}/bookings/{booking}?user_id={userId}</code>
 * </p>
 */
public final class UrlTemplate {

    private final String value;

    @JsonCreator
    public UrlTemplate(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        UrlTemplate other = (UrlTemplate) object;

        return Objects.equals(this.value, other.value);
    }

    public static UrlTemplate of(String value) {
        return new UrlTemplate(value);
    }

}
