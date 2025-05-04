package com.extole.appboot.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaintenanceRequest {

    private final MaintenanceState state;

    public MaintenanceRequest(@JsonProperty("state") MaintenanceState state) {
        this.state = state;
    }

    @JsonProperty("state")
    public MaintenanceState getState() {
        return state;
    }
}
