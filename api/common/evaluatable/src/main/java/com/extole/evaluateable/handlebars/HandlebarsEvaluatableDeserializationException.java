package com.extole.evaluateable.handlebars;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class HandlebarsEvaluatableDeserializationException extends MismatchedInputException {
    private final HandlebarsExpressionCompileTimeException cause;

    public HandlebarsEvaluatableDeserializationException(JsonParser parser,
        HandlebarsExpressionCompileTimeException cause) {
        super(parser, cause.getMessage());
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return super.getOriginalMessage();
    }

    @Override
    public synchronized HandlebarsExpressionCompileTimeException getCause() {
        return cause;
    }
}
