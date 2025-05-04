package com.extole.client.rest.rewards;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class RewardUpdateRequest {

    private static final String SANDBOX = "sandbox";

    private final Omissible<String> sandbox;

    public RewardUpdateRequest(@JsonProperty(SANDBOX) Omissible<String> sandbox) {
        this.sandbox = sandbox;
    }

    @JsonProperty(SANDBOX)
    public Omissible<String> getSandbox() {
        return sandbox;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
