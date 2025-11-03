package com.extole.client.rest.component.facet;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ComponentFacetResponse {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String ALLOWED_VALUES = "allowed_values";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String name;
    private final Optional<String> displayName;
    private final List<ComponentFacetAllowedValueResponse> allowedValues;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public ComponentFacetResponse(
        @JsonProperty(NAME) String name,
        @JsonProperty(DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(ALLOWED_VALUES) List<ComponentFacetAllowedValueResponse> allowedValues,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.name = name;
        this.displayName = displayName;
        this.allowedValues = allowedValues;
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

    @JsonProperty(ALLOWED_VALUES)
    public List<ComponentFacetAllowedValueResponse> getAllowedValues() {
        return allowedValues;
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
