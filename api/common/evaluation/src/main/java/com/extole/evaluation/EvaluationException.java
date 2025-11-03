package com.extole.evaluation;

import com.extole.common.lang.exception.NotifiableException;
import com.extole.evaluateable.Evaluatable;

@NotifiableException(notifiableRuntimeCauses = {
    "org.openjdk.nashorn.api.scripting.NashornException",
    "org.springframework.expression.spel.SpelEvaluationException",
    "com.extole.common.variable.evaluator.impl.LoopPreventingVariableEvaluatorRuntimeException",
    "java.lang.ClassCastException",
    "com.extole.api.impl.NullArgumentRuntimeException",
    "com.extole.consumer.event.service.impl.IllegalStateConsumerEventSenderExecutorRuntimeException",
    "java.lang.NumberFormatException",
    "java.lang.IllegalArgumentException",
    "com.extole.api.impl.ContextApiRuntimeException"})
public class EvaluationException extends Exception {

    private final Evaluatable<?, ?> evaluatable;

    EvaluationException(String message, Evaluatable<?, ?> evaluatable) {
        super(message);
        this.evaluatable = evaluatable;
    }

    EvaluationException(String message, Evaluatable<?, ?> evaluatable, Throwable cause) {
        super(message, cause);
        this.evaluatable = evaluatable;
    }

    public Evaluatable<?, ?> getEvaluatable() {
        return evaluatable;
    }

}
