package com.extole.consumer.rest.me;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeDataUpdateRequest {

    // TODO rename to scope ENG-13506
    private static final String JSON_PROPERTY_TYPE = "type";
    private static final String JSON_PROPERTY_VALUE = "value";

    private final MeDataType type;
    private final Object value;

    @JsonCreator
    public MeDataUpdateRequest(@JsonProperty(JSON_PROPERTY_TYPE) MeDataType type,
        @JsonProperty(JSON_PROPERTY_VALUE) Object value) {
        this.type = type;
        this.value = value;
    }

    @Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    public MeDataType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    public Object getValue() {
        return value;
    }

}
