package com.extole.evaluateable.spel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class SpelEvaluatableDeserializationException extends MismatchedInputException {
    private final SpelExpressionCompileTimeException cause;

    public SpelEvaluatableDeserializationException(JsonParser parser, SpelExpressionCompileTimeException cause) {
        super(parser, cause.getMessage());
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return super.getOriginalMessage();
    }

    @Override
    public synchronized SpelExpressionCompileTimeException getCause() {
        return cause;
    }
}
