package com.extole.consumer.rest.optout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class OptoutResponse {
    private static final String OPTOUT = "optout";

    private final Boolean optout;

    @JsonCreator
    public OptoutResponse(@JsonProperty(OPTOUT) Boolean optout) {
        this.optout = optout;
    }

    @JsonProperty(OPTOUT)
    public Boolean getOptout() {
        return optout;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
