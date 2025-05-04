package com.extole.appboot.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaintenanceResponse {

    private final MaintenanceState state;

    public MaintenanceResponse(@JsonProperty("state") MaintenanceState state) {
        this.state = state;
    }

    @JsonProperty("state")
    public MaintenanceState getState() {
        return state;
    }
}
