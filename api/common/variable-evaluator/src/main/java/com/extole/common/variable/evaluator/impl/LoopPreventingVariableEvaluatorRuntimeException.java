package com.extole.common.variable.evaluator.impl;

public class LoopPreventingVariableEvaluatorRuntimeException extends RuntimeException {

    public LoopPreventingVariableEvaluatorRuntimeException(String message) {
        super(message);
    }

    public LoopPreventingVariableEvaluatorRuntimeException(Throwable cause) {
        super(cause);
    }
}
