package com.extole.api.model.campaign.built;

import com.extole.api.step.data.StepDataContext;
import com.extole.evaluateable.RuntimeEvaluatable;

public interface BuiltStepData {

    String getId();

    String getName();

    RuntimeEvaluatable<StepDataContext, Object> getValue();

    String getScope();

    boolean isDimension();

    String[] getPersistTypes();

    RuntimeEvaluatable<StepDataContext, Object> getDefaultValue();

    String getKeyType();

    boolean getEnabled();

}
