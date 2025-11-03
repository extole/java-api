package com.extole.client.identity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

final class IdentityKeyImpl implements IdentityKey {
    private final String name;

    private IdentityKeyImpl(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String getName() {
        return name;
    }

    @JsonCreator
    static IdentityKeyImpl valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value can't be null");
        }
        return new IdentityKeyImpl(value);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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

        IdentityKeyImpl other = (IdentityKeyImpl) object;
        return StringUtils.equalsIgnoreCase(name, other.name);
    }

}
