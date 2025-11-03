package com.extole.common.variable.evaluator;

import java.util.Optional;

import com.extole.api.impl.campaign.RuntimeFlatSetting;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluation.EvaluationException;

public interface RuntimeVariableEvaluationService {

    <CONTEXT> Optional<Object> evaluate(Evaluatable<CONTEXT, Optional<Object>> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier, RuntimeFlatSetting variable) throws EvaluationException;

}
