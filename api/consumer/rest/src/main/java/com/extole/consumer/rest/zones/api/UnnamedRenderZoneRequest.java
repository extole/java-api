package com.extole.consumer.rest.zones.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import com.extole.common.lang.ToString;

public class UnnamedRenderZoneRequest {

    private final Map<String, Object> data;

    @JsonCreator
    public UnnamedRenderZoneRequest() {
        this.data = new HashMap<>();
    }

    public UnnamedRenderZoneRequest(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    @JsonValue
    public Map<String, Object> getData() {
        return data;
    }

    @JsonAnySetter
    public void addData(String key,
        Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
