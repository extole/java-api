package com.extole.client.rest.component.facet;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ComponentFacetAllowedValueCreateRequest {

    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_ICON = "icon";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_DISPLAY_NAME = "display_name";
    private static final String PROPERTY_COLOR = "color";

    private final String value;
    private final Optional<String> icon;
    private final Optional<String> description;
    private final Optional<String> displayName;
    private final Optional<String> color;

    @JsonCreator
    public ComponentFacetAllowedValueCreateRequest(
        @JsonProperty(PROPERTY_VALUE) String value,
        @JsonProperty(PROPERTY_ICON) Optional<String> icon,
        @JsonProperty(PROPERTY_DESCRIPTION) Optional<String> description,
        @JsonProperty(PROPERTY_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(PROPERTY_COLOR) Optional<String> color) {
        this.value = value;
        this.icon = icon;
        this.description = description;
        this.displayName = displayName;
        this.color = color;
    }

    @JsonProperty(PROPERTY_VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(PROPERTY_ICON)
    public Optional<String> getIcon() {
        return icon;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(PROPERTY_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(PROPERTY_COLOR)
    public Optional<String> getColor() {
        return color;
    }

    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ComponentFacetAllowedValueCreateRequest facet) {
        return new Builder(facet);
    }

    public static final class Builder {
        private String value;
        private Optional<String> icon = Optional.empty();
        private Optional<String> description = Optional.empty();
        private Optional<String> displayName = Optional.empty();
        private Optional<String> color = Optional.empty();

        private Builder() {
        }

        private Builder(ComponentFacetAllowedValueCreateRequest componentFacet) {
            this.value = componentFacet.getValue();
            this.icon = componentFacet.getIcon();
            this.description = componentFacet.getDescription();
            this.displayName = componentFacet.getDisplayName();
            this.color = componentFacet.getColor();
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = Optional.of(icon);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Optional.of(description);
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Optional.of(displayName);
            return this;
        }

        public Builder withColor(String color) {
            this.color = Optional.of(color);
            return this;
        }

        public ComponentFacetAllowedValueCreateRequest build() {
            return new ComponentFacetAllowedValueCreateRequest(value, icon, description, displayName, color);
        }

    }

}
