package com.extole.client.rest.campaign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import com.extole.common.lang.ToString;

public class StepResponse {
    private static final String NAME = "name";

    private final String name;

    public StepResponse(@JsonProperty(NAME) String name) {
        this.name = name;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        StepResponse that = (StepResponse) object;
        return Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
