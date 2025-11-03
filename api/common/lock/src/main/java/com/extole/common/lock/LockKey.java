package com.extole.common.lock;

import java.util.Objects;

import com.extole.id.Id;

public class LockKey {
    private final String value;

    public LockKey(String value) {
        this.value = value;
    }

    public LockKey(String prefix, Id<?> id) {
        this.value = prefix + "-" + id.getValue();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || otherObject.getClass() != getClass()) {
            return false;
        }

        LockKey other = (LockKey) otherObject;
        return Objects.equals(value, other.value);
    }
}
