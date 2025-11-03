package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonJourneyDataResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUE = "value";

    private final String id;
    private final String name;
    private final PersonDataScope scope;
    private final Object value;

    @JsonCreator
    public PersonJourneyDataResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_SCOPE) PersonDataScope scope,
        @JsonProperty(JSON_VALUE) Object value) {
        this.id = id;
        this.name = name;
        this.scope = scope;
        this.value = value;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
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
