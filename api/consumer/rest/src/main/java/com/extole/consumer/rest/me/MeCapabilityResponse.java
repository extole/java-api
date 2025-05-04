package com.extole.consumer.rest.me;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.Scope;

public class MeCapabilityResponse {
    private Set<Scope> capabilities;

    @JsonCreator
    public MeCapabilityResponse(@JsonProperty("capabilities") Set<Scope> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonProperty("capabilities")
    public Set<Scope> getCapabilities() {
        return capabilities;
    }
}
