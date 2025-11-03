package com.extole.api.step.campaign.step.data;

import java.util.Optional;

import com.extole.api.step.data.StepDataContext;
import com.extole.evaluateable.RuntimeEvaluatable;

public interface StepData {

    String getName();

    RuntimeEvaluatable<StepDataContext, Optional<Object>> getValue();

}
