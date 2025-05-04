package com.extole.client.rest.component.type;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentTypeResponse {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String SCHEMA = "schema";
    private static final String PARENT = "parent";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String name;
    private final Optional<String> displayName;
    private final String schema;
    private final Optional<String> parent;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public ComponentTypeResponse(@JsonProperty(NAME) String name,
        @JsonProperty(DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(SCHEMA) String schema,
        @JsonProperty(PARENT) Optional<String> parent,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.name = name;
        this.displayName = displayName;
        this.schema = schema;
        this.parent = parent;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(SCHEMA)
    public String getSchema() {
        return schema;
    }

    @JsonProperty(PARENT)
    public Optional<String> getParent() {
        return parent;
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
