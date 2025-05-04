package com.extole.client.rest.person;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonDataResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VALUE = "value";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_UPDATED_DATE = "updated_date";

    private final String name;
    private final PersonDataScope scope;
    private final Object value;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public PersonDataResponse(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_SCOPE) PersonDataScope scope,
        @JsonProperty(JSON_VALUE) Object value,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate) {
        this.name = name;
        this.scope = scope;
        this.value = value;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
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

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
