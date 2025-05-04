package com.extole.client.rest.v0;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DataResponse {
    private static final String JSON_DATA = "data";

    // only ever one value in response
    private final List<ActionDetailResponse> data;

    @JsonCreator
    public DataResponse(@JsonProperty(JSON_DATA) List<ActionDetailResponse> actionDetailResponses) {
        this.data = actionDetailResponses;
    }

    @JsonProperty(JSON_DATA)
    public List<ActionDetailResponse> getActionDetail() {
        return data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
