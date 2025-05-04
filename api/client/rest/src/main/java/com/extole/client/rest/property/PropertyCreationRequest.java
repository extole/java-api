package com.extole.client.rest.property;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyCreationRequest {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private final String name;
    private final String value;

    public PropertyCreationRequest(@JsonProperty(NAME) String name, @JsonProperty(VALUE) String value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

}
