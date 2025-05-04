package com.extole.client.rest.property;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyUpdateRequest {

    private static final String VALUE = "value";
    private final String value;

    public PropertyUpdateRequest(@Nullable @JsonProperty(VALUE) String value) {
        this.value = value;
    }

    @Nullable
    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

}
