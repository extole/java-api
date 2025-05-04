package com.extole.client.rest.subcription;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class UserSubscriptionTypeResponse {
    private static final String NAME = "name";

    private final String name;

    public UserSubscriptionTypeResponse(@JsonProperty(NAME) String name) {
        this.name = name;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
