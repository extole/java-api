package com.extole.common.journey;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public final class JourneyName {

    public static final JourneyName ADVOCATE = valueOf("ADVOCATE");
    public static final JourneyName FRIEND = valueOf("FRIEND");
    public static final JourneyName PARTICIPANT = valueOf("participant");

    private final String value;

    private JourneyName(String value) {
        this.value = value.trim();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JourneyName valueOf(String value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        return new JourneyName(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
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

        JourneyName other = (JourneyName) object;
        return StringUtils.equalsIgnoreCase(value, other.value);
    }

}
