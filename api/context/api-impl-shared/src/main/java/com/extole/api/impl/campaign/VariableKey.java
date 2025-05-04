package com.extole.api.impl.campaign;

import java.util.Objects;

import com.extole.common.lang.ToString;

public final class VariableKey {

    private final String name;
    private final String key;

    private VariableKey(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public static VariableKey of(String name, String key) {
        return new VariableKey(name, key);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        VariableKey that = (VariableKey) other;
        return Objects.equals(name.toLowerCase(), that.name.toLowerCase()) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), key);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
