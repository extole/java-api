package com.extole.client.rest.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientPropertyResponse {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String SCOPE = "scope";
    private final String name;
    private final String value;
    private final ClientPropertyScope scope;

    @JsonCreator
    public ClientPropertyResponse(@JsonProperty(NAME) String name,
        @JsonProperty(VALUE) String value,
        @JsonProperty(SCOPE) ClientPropertyScope scope) {
        this.name = name;
        this.value = value;
        this.scope = scope;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(SCOPE)
    public ClientPropertyScope getScope() {
        return scope;
    }
}
