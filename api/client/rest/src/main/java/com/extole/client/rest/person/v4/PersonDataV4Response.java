package com.extole.client.rest.person.v4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.PersonDataScope;
import com.extole.common.lang.ToString;

public class PersonDataV4Response {

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final PersonDataScope scope;
    private final Object value;

    @JsonCreator
    public PersonDataV4Response(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_SCOPE) PersonDataScope scope,
        @JsonProperty(JSON_VALUE) Object value) {
        this.name = name;
        this.scope = scope;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_SCOPE)
    public PersonDataScope getScope() {
        return scope;
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
