package com.extole.id;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

public final class Id<T> implements Serializable {
    private final String value;

    private Id(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static <T> Id<T> valueOf(String value) {
        Preconditions.checkNotNull(value, "Id value cannot be null");
        return new Id<>(value);
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
        Id<?> other = (Id<?>) object;
        return Objects.equals(value, other.value);
    }
}
