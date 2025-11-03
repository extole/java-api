package com.extole.evaluation;

import com.extole.evaluateable.Evaluatable;

public class HandlebarsInvalidSyntaxEvaluationResultException extends InvalidEvaluationResultException {

    HandlebarsInvalidSyntaxEvaluationResultException(Evaluatable<?, ?> evaluatable, Throwable throwable) {
        super(throwable.getMessage(), evaluatable, throwable);
    }

}
