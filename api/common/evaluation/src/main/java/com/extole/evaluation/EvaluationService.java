package com.extole.evaluation;

import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.evaluateable.Evaluatable;

public interface EvaluationService {

    @Deprecated // TODO remove this ENG-21229
    ThreadLocal<Evaluatable<?, ?>> CURRENT_EVALUATABLE = new InheritableThreadLocal<>();

    <CONTEXT, RESULT> RESULT evaluate(Evaluatable<CONTEXT, RESULT> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier) throws EvaluationException;

}
