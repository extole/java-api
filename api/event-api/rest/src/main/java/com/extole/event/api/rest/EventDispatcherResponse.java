package com.extole.event.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class EventDispatcherResponse {
    private static final String JSON_ID = "id";

    private final String id;

    public EventDispatcherResponse(
        @JsonProperty(JSON_ID) String id) {
        this.id = id;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
