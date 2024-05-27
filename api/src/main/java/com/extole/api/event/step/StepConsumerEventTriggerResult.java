package com.extole.api.event.step;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface StepConsumerEventTriggerResult {

    String getTriggerId();

    String getTriggerType();

    StepConsumerEventTriggerPhase getTriggerPhase();

    String getName();

    boolean isPassed();

    String[] getLogMessages();

}
