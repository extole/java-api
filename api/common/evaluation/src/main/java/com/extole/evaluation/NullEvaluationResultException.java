package com.extole.evaluation;

import com.extole.evaluateable.Evaluatable;

public class NullEvaluationResultException extends InvalidEvaluationResultException {

    NullEvaluationResultException(Evaluatable<?, ?> evaluatable) {
        super("Evaluation output is null for non optional result type", evaluatable);
    }

}
