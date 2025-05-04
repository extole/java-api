package com.extole.api.trigger.legacy.quality;

import javax.annotation.Nullable;

import com.extole.api.evaluation.result.EvaluationResultBuilder;
import com.extole.api.person.Person;
import com.extole.api.person.PersonReferral;
import com.extole.api.trigger.StepTriggerContext;

public interface LegacyQualityRuleTriggerContext extends StepTriggerContext {

    @Nullable
    PersonReferral getBestReferral();

    @Nullable
    Person findPersonById(String personId);

    @Nullable
    Object jsonPath(Object object, String path);

    boolean isFriendController();

    EvaluationResultBuilder getResultBuilder();

}
