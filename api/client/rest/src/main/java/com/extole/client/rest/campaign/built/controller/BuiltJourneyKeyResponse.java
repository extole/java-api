package com.extole.client.rest.campaign.built.controller;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.journey.JourneyKeyContext;
import com.extole.evaluateable.RuntimeEvaluatable;

public class BuiltJourneyKeyResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final String name;
    private final RuntimeEvaluatable<JourneyKeyContext, Optional<Object>> value;

    public BuiltJourneyKeyResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) RuntimeEvaluatable<JourneyKeyContext, Optional<Object>> value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public RuntimeEvaluatable<JourneyKeyContext, Optional<Object>> getValue() {
        return value;
    }

}
