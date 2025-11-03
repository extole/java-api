package com.extole.client.rest.component.facet;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ComponentFacetCreateRequest {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String ALLOWED_VALUES = "allowed_values";

    private final String name;
    private final Optional<String> displayName;
    private final List<ComponentFacetAllowedValueCreateRequest> allowedValues;

    @JsonCreator
    public ComponentFacetCreateRequest(
        @JsonProperty(NAME) String name,
        @JsonProperty(DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(ALLOWED_VALUES) List<ComponentFacetAllowedValueCreateRequest> allowedValues) {
        this.name = name;
        this.displayName = displayName;
        this.allowedValues = allowedValues;
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
    public List<ComponentFacetAllowedValueCreateRequest> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private Optional<String> displayName = Optional.empty();
        private List<ComponentFacetAllowedValueCreateRequest> allowedValues;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Optional.of(displayName);
            return this;

        }

        public Builder withAllowedValues(List<ComponentFacetAllowedValueCreateRequest> allowedValues) {
            this.allowedValues = allowedValues;
            return this;
        }

        public ComponentFacetCreateRequest build() {
            return new ComponentFacetCreateRequest(name, displayName, allowedValues);
        }
    }

}
