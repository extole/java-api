package com.extole.api.step.data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface StepTriggerResult {

    String getId();

    String getType();

    String getPhase();

    String getName();

    boolean isPassed();

    String[] getLogMessages();

}
