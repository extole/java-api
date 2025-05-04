package com.extole.client.rest.person.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.PersonDataScope;

public class PersonParametersUpdateV2Request {

    private static final String JSON_PROPERTY_SCOPE = "scope";
    private static final String JSON_PROPERTY_VALUE = "value";

    private final PersonDataScope scope;
    private final Object value;

    @JsonCreator
    public PersonParametersUpdateV2Request(@JsonProperty(JSON_PROPERTY_SCOPE) PersonDataScope scope,
        @JsonProperty(JSON_PROPERTY_VALUE) Object value) {
        this.scope = scope;
        this.value = value;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_SCOPE)
    public PersonDataScope getScope() {
        return scope;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

}
