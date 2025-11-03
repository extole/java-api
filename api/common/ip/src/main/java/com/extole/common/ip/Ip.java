package com.extole.common.ip;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

public final class Ip implements Serializable {
    public static final Ip UNKNOWN_IP = new Ip("0.0.0.0");

    private final String value;

    private Ip(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Ip valueOf(String value) {
        Preconditions.checkNotNull(value, "Ip address cannot be null");
        return new Ip(value);
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
        Ip other = (Ip) object;
        return Objects.equals(value, other.value);
    }
}
