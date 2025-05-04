package com.extole.consumer.rest.debug;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CreativeMetricResponse {
    private static final String ID = "id";
    private final String id;

    public CreativeMetricResponse(@JsonProperty(ID) String id) {
        this.id = id;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
