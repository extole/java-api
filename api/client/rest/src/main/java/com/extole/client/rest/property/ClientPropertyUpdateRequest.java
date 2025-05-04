package com.extole.client.rest.property;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ClientPropertyUpdateRequest {

    private static final String VALUE = "value";
    private static final String SCOPE = "scope";
    private final Omissible<String> value;
    private final Omissible<Optional<ClientPropertyScope>> scope;

    public ClientPropertyUpdateRequest(@JsonProperty(VALUE) Omissible<String> value,
        @JsonProperty(SCOPE) Omissible<Optional<ClientPropertyScope>> scope) {
        this.value = value;
        this.scope = scope;
    }

    @JsonProperty(VALUE)
    public Omissible<String> getValue() {
        return value;
    }

    @JsonProperty(SCOPE)
    public Omissible<Optional<ClientPropertyScope>> getScope() {
        return scope;
    }

}
