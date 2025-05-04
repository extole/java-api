package com.extole.client.rest.optout;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class OptoutResponse {

    private static final String EMAIL = "email";
    private static final String OPTOUT = "optout";

    private final String email;
    private final Boolean optout;

    public OptoutResponse(@JsonProperty(EMAIL) String email,
        @JsonProperty(OPTOUT) Boolean optout) {
        this.email = email;
        this.optout = optout;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
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
