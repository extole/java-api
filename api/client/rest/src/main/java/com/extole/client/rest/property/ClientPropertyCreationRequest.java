package com.extole.client.rest.property;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ClientPropertyCreationRequest {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String SCOPE = "scope";
    private final String name;
    private final String value;
    private final Omissible<ClientPropertyScope> scope;

    public ClientPropertyCreationRequest(@JsonProperty(NAME) String name, @JsonProperty(VALUE) String value,
        @JsonProperty(SCOPE) Omissible<ClientPropertyScope> scope) {
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
    public Omissible<ClientPropertyScope> getScope() {
        return scope;
    }

}
