package com.extole.api.step.action;

import com.extole.api.event.step.StepConsumerEvent;
import com.extole.api.step.StepContext;

public interface StepActionContext extends StepContext {

    StepConsumerEvent getStepEvent();

}
