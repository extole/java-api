package com.extole.client.rest.client.variables;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientVariableResponse {
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String name;
    private final Optional<String> value;
    private final Optional<String> description;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public ClientVariableResponse(@JsonProperty(NAME) String name,
        @JsonProperty(VALUE) Optional<String> value,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(VALUE)
    public Optional<String> getValue() {
        return value;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
