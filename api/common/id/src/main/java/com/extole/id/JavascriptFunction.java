package com.extole.id;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public final class JavascriptFunction<INPUT, OUTPUT> {
    private final String value;

    @JsonCreator
    public JavascriptFunction(String value) {
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
        JavascriptFunction<?, ?> other = (JavascriptFunction<?, ?>) object;
        return Objects.equals(this.value, other.value);
    }
}
