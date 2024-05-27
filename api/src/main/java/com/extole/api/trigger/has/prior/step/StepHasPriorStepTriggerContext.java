package com.extole.api.trigger.has.prior.step;

import com.extole.api.person.PersonStep;

public interface StepHasPriorStepTriggerContext extends HasPriorStepTriggerContext {

    PersonStep getStep();

}
