package com.extole.client.rest.person.shareables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonShareableDataResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final Object value;

    @JsonCreator
    public PersonShareableDataResponse(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) Object value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
