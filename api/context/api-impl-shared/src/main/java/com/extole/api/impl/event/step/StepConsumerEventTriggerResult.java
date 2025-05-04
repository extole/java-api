package com.extole.api.impl.event.step;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.event.step.StepConsumerEventTriggerPhase;
import com.extole.common.lang.ToString;

public class StepConsumerEventTriggerResult implements com.extole.api.event.step.StepConsumerEventTriggerResult {

    private static final String JSON_TRIGGER_ID = "trigger_id";
    private static final String JSON_TRIGGER_TYPE = "trigger_type";
    private static final String JSON_NAME = "name";
    private static final String JSON_PASSED = "passed";
    private static final String JSON_LOG_MESSAGES = "log_messages";
    private static final String JSON_TRIGGER_PHASE = "trigger_phase";

    private final String triggerId;
    private final String triggerType;
    private final String name;
    private final boolean passed;
    private final String[] logMessages;
    private final StepConsumerEventTriggerPhase triggerPhase;

    StepConsumerEventTriggerResult(com.extole.event.consumer.step.StepConsumerEventTriggerResult triggerResult) {
        this.triggerId = triggerResult.getTriggerId();
        this.triggerType = triggerResult.getTriggerType();
        this.triggerPhase = StepConsumerEventTriggerPhase.valueOf(triggerResult.getTriggerPhase().name());
        this.name = triggerResult.getName();
        this.passed = triggerResult.isPassed();
        this.logMessages = triggerResult.getLogMessages().toArray(new String[] {});
    }

    @JsonProperty(JSON_TRIGGER_ID)
    public String getTriggerId() {
        return triggerId;
    }

    @JsonProperty(JSON_TRIGGER_TYPE)
    public String getTriggerType() {
        return triggerType;
    }

    @JsonProperty(JSON_TRIGGER_PHASE)
    public StepConsumerEventTriggerPhase getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PASSED)
    public boolean isPassed() {
        return passed;
    }

    @JsonProperty(JSON_LOG_MESSAGES)
    public String[] getLogMessages() {
        return logMessages;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
