package com.extole.common.event;

import com.google.common.base.Objects;

import com.extole.id.Id;

public class PartitionKey {
    private final String value;

    public PartitionKey(String value) {
        this.value = value;
    }

    public PartitionKey(Id<?> id) {
        this.value = id.getValue();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        PartitionKey that = (PartitionKey) other;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
