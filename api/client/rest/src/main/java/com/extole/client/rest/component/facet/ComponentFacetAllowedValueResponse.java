package com.extole.client.rest.component.facet;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ComponentFacetAllowedValueResponse {

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
    public ComponentFacetAllowedValueResponse(
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

}
