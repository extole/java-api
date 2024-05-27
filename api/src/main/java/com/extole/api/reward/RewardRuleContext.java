package com.extole.api.reward;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.evaluation.result.EvaluationResultBuilder;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.person.Person;

public interface RewardRuleContext extends GlobalContext, LoggerContext {

    Person getPerson();

    ConsumerEvent getEvent();

    @Nullable
    Object jsonPath(Object object, String path);

    EvaluationResultBuilder getResultBuilder();
}
