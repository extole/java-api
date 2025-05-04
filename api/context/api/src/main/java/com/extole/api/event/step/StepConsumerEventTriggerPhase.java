package com.extole.api.event.step;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum StepConsumerEventTriggerPhase {
    MATCHING,
    QUALIFYING,
    QUALITY
}
