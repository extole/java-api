package com.extole.client.rest.component.facet;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class ComponentFacetAllowedValueUpdateRequest {

    private static final String PROPERTY_VALUE = "value";
    private static final String PROPERTY_ICON = "icon";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_DISPLAY_NAME = "display_name";
    private static final String PROPERTY_COLOR = "color";

    private final Omissible<String> value;
    private final Omissible<Optional<String>> icon;
    private final Omissible<Optional<String>> description;
    private final Omissible<Optional<String>> displayName;
    private final Omissible<Optional<String>> color;

    @JsonCreator
    public ComponentFacetAllowedValueUpdateRequest(
        @JsonProperty(PROPERTY_VALUE) Omissible<String> value,
        @JsonProperty(PROPERTY_ICON) Omissible<Optional<String>> icon,
        @JsonProperty(PROPERTY_DESCRIPTION) Omissible<Optional<String>> description,
        @JsonProperty(PROPERTY_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(PROPERTY_COLOR) Omissible<Optional<String>> color) {
        this.value = value;
        this.icon = icon;
        this.description = description;
        this.displayName = displayName;
        this.color = color;
    }

    @JsonProperty(PROPERTY_VALUE)
    public Omissible<String> getValue() {
        return value;
    }

    @JsonProperty(PROPERTY_ICON)
    public Omissible<Optional<String>> getIcon() {
        return icon;
    }

    @JsonProperty(PROPERTY_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(PROPERTY_DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(PROPERTY_COLOR)
    public Omissible<Optional<String>> getColor() {
        return color;
    }

    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ComponentFacetAllowedValueUpdateRequest facet) {
        return new Builder(facet);
    }

    public static final class Builder {
        private Omissible<String> value = Omissible.omitted();
        private Omissible<Optional<String>> icon = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Optional<String>> displayName = Omissible.omitted();
        private Omissible<Optional<String>> color = Omissible.omitted();

        private Builder() {
        }

        private Builder(ComponentFacetAllowedValueUpdateRequest componentFacet) {
            this.value = componentFacet.getValue();
            this.icon = componentFacet.getIcon();
            this.description = componentFacet.getDescription();
            this.displayName = componentFacet.getDisplayName();
            this.color = componentFacet.getColor();
        }

        public Builder withValue(String value) {
            this.value = Omissible.of(value);
            return this;
        }

        public Builder withIcon(Optional<String> icon) {
            this.icon = Omissible.of(icon);
            return this;
        }

        public Builder withDescription(Optional<String> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withDisplayName(Optional<String> displayName) {
            this.displayName = Omissible.of(displayName);
            return this;
        }

        public Builder withColor(Optional<String> color) {
            this.color = Omissible.of(color);
            return this;
        }

        public ComponentFacetAllowedValueUpdateRequest build() {
            return new ComponentFacetAllowedValueUpdateRequest(value, icon, description, displayName, color);
        }

    }

}
