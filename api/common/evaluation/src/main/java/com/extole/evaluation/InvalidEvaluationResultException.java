package com.extole.evaluation;

import com.extole.evaluateable.Evaluatable;

public class InvalidEvaluationResultException extends EvaluationException {
    InvalidEvaluationResultException(String message, Evaluatable<?, ?> evaluatable) {
        super(message, evaluatable);
    }

    InvalidEvaluationResultException(String message, Evaluatable<?, ?> evaluatable, Throwable cause) {
        super(message, evaluatable, cause);
    }
}
