package com.extole.common.ip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class City {

    private static final String NAME = "name";

    private final String name;

    @JsonCreator
    public City(@JsonProperty(NAME) String name) {
        this.name = name;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
