package com.extole.consumer.rest.signal.step;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class StepSignalResponse {

    private static final String POLLING_ID = "polling_id";
    private static final String SIGNALS = "signals";

    private final String pollingId;
    private final List<StepSignal> stepSignals;

    @JsonCreator
    public StepSignalResponse(@JsonProperty(POLLING_ID) String pollingId,
        @JsonProperty(SIGNALS) List<StepSignal> stepSignals) {
        this.pollingId = pollingId;
        this.stepSignals = ImmutableList.copyOf(stepSignals);
    }

    @JsonProperty(POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty(SIGNALS)
    public List<StepSignal> getStepSignals() {
        return stepSignals;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
