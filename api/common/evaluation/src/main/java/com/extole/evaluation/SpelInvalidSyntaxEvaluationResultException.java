package com.extole.evaluation;

import com.extole.evaluateable.Evaluatable;

public class SpelInvalidSyntaxEvaluationResultException extends InvalidEvaluationResultException {

    SpelInvalidSyntaxEvaluationResultException(Evaluatable<?, ?> evaluatable, Throwable throwable) {
        super(throwable.getMessage(), evaluatable, throwable);
    }

}
