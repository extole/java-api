package com.extole.evaluation;

import com.extole.evaluateable.Evaluatable;

public class EvaluationRuntimeException extends RuntimeException {

    private Evaluatable<?, ?> evaluatable;

    protected EvaluationRuntimeException(String message, Evaluatable<?, ?> evaluatable, Throwable cause) {
        super(message, cause);
        this.evaluatable = evaluatable;
    }

    public Evaluatable<?, ?> getEvaluatable() {
        return evaluatable;
    }
}
