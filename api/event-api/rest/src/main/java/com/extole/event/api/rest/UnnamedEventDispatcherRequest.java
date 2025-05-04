package com.extole.event.api.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Maps;

import com.extole.common.lang.ToString;

public class UnnamedEventDispatcherRequest {
    private final Map<String, Object> data;

    @JsonCreator
    public UnnamedEventDispatcherRequest() {
        this.data = Maps.newHashMap();
    }

    public UnnamedEventDispatcherRequest(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    @JsonValue
    public Map<String, Object> getData() {
        return data;
    }

    @JsonAnySetter
    public void addData(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
