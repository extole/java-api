package com.extole.optout.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class OptoutResponse {
    private static final String RESULT = "result";

    private final Boolean result;

    @JsonCreator
    public OptoutResponse(@JsonProperty(RESULT) Boolean result) {
        this.result = result;
    }

    @JsonIgnore
    public Boolean getResult() {
        return result;
    }

    @JsonProperty(RESULT)
    public String getResultAsString() {
        return result.toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
