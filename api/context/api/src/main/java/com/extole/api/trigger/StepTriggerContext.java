package com.extole.api.trigger;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.step.StepContext;
import com.extole.api.step.TargetingContext;

@Schema
public interface StepTriggerContext extends StepContext {

    TargetingContext getTargetingContext();

}
