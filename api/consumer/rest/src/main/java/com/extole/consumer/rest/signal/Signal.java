package com.extole.consumer.rest.signal;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class Signal {

    private static final String JSON_NAME = "name";
    private static final String JSON_DATA = "data";

    private final String name;
    private final Map<String, Object> data;

    @JsonCreator
    public Signal(@JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_DATA) Map<String, Object> data) {
        this.name = name;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
