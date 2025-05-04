package com.extole.client.rest.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class StepsResponse {
    private static final String STEPS = "steps";
    private final List<StepResponse> steps;

    public StepsResponse(@JsonProperty(STEPS) List<StepResponse> steps) {
        this.steps = ImmutableList.copyOf(steps);
    }

    @JsonProperty(STEPS)
    public List<StepResponse> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
