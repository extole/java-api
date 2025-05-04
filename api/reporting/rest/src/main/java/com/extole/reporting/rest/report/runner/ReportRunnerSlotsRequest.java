package com.extole.reporting.rest.report.runner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportRunnerSlotsRequest {
    private static final String JSON_SLOT = "slot";

    private final String slot;

    @JsonCreator
    public ReportRunnerSlotsRequest(@JsonProperty(JSON_SLOT) String slot) {
        this.slot = slot;
    }

    @JsonProperty(JSON_SLOT)
    public String getSlot() {
        return slot;
    }
}
