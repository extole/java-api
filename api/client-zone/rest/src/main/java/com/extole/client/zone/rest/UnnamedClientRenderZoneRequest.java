package com.extole.client.zone.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class UnnamedClientRenderZoneRequest {

    private final Map<String, Object> data;

    @JsonCreator
    public UnnamedClientRenderZoneRequest() {
        this.data = new HashMap<>();
    }

    public UnnamedClientRenderZoneRequest(Map<String, Object> data) {
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

}
