package com.extole.api.impl.campaign;

public final class FlatVariableEvaluationRuntimeException extends RuntimeException {
    public FlatVariableEvaluationRuntimeException(Exception cause, String message) {
        super(message, cause);
    }
}
