package com.extole.client.rest.flow;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class InputFlowStepTriggerResponse {

    private static final String TRIGGER_TYPE = "trigger_type";
    private static final String EVENT_NAMES = "event_names";

    private final FlowStepTriggerType type;
    private final Set<String> eventNames;

    public InputFlowStepTriggerResponse(
        @JsonProperty(TRIGGER_TYPE) FlowStepTriggerType type,
        @JsonProperty(EVENT_NAMES) Set<String> eventNames) {
        this.type = type;
        this.eventNames = eventNames;
    }

    @JsonProperty(TRIGGER_TYPE)
    public FlowStepTriggerType getType() {
        return type;
    }

    @JsonProperty(EVENT_NAMES)
    public Set<String> getEventNames() {
        return eventNames;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
