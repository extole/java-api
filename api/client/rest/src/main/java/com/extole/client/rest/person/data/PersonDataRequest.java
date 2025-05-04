package com.extole.client.rest.person.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.person.PersonDataScope;
import com.extole.common.lang.ToString;

public class PersonDataRequest {

    private static final String JSON_PROPERTY_NAME = "name";
    private static final String JSON_PROPERTY_VALUE = "value";
    private static final String JSON_PROPERTY_SCOPE = "scope";

    private final String name;
    private final Object value;
    private final PersonDataScope scope;

    @JsonCreator
    public PersonDataRequest(
        @JsonProperty(JSON_PROPERTY_NAME) String name,
        @JsonProperty(JSON_PROPERTY_VALUE) Object value,
        @JsonProperty(JSON_PROPERTY_SCOPE) PersonDataScope scope) {
        this.name = name;
        this.value = value;
        this.scope = scope;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_SCOPE)
    public PersonDataScope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
