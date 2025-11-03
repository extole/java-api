package com.extole.id;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class PrimaryKey<T> {
    private final Long value;

    public PrimaryKey(Long value) {
        Preconditions.checkNotNull(value, "PrimaryKey value cannot be null");
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    /**
     * Shorthand version of constructor that avoids diamond operator.
     */
    public static <T> PrimaryKey<T> valueOf(Long value) {
        return new PrimaryKey<T>(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (null == other || !getClass().equals(other.getClass())) {
            return false;
        }

        PrimaryKey<?> that = (PrimaryKey<?>) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
