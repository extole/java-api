package com.extole.client.rest.component.facet;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class ComponentFacetUpdateRequest {

    private static final String DISPLAY_NAME = "display_name";
    private static final String ALLOWED_VALUES = "allowed_values";

    private final Omissible<Optional<String>> displayName;
    private final Omissible<List<ComponentFacetAllowedValueUpdateRequest>> allowedValues;

    @JsonCreator
    public ComponentFacetUpdateRequest(
        @JsonProperty(DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(ALLOWED_VALUES) Omissible<List<ComponentFacetAllowedValueUpdateRequest>> allowedValues) {
        this.displayName = displayName;
        this.allowedValues = allowedValues;
    }

    @JsonProperty(DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(ALLOWED_VALUES)
    public Omissible<List<ComponentFacetAllowedValueUpdateRequest>> getAllowedValues() {
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

        private Omissible<Optional<String>> displayName = Omissible.omitted();
        private Omissible<List<ComponentFacetAllowedValueUpdateRequest>> allowedValues;

        private Builder() {
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(Optional.of(displayName));
            return this;
        }

        public Builder clearDisplayName() {
            this.displayName = Omissible.of(Optional.empty());
            return this;
        }

        public Builder withAllowedValues(Omissible<List<ComponentFacetAllowedValueUpdateRequest>> allowedValues) {
            this.allowedValues = allowedValues;
            return this;
        }

        public ComponentFacetUpdateRequest build() {
            return new ComponentFacetUpdateRequest(displayName, allowedValues);
        }
    }

}
