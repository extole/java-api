package com.extole.api.trigger.has.prior.reward;

import com.extole.api.person.PersonReward;
import com.extole.api.trigger.StepTriggerContext;

public interface HasPriorRewardTriggerContext extends StepTriggerContext {

    PersonReward getReward();

}
