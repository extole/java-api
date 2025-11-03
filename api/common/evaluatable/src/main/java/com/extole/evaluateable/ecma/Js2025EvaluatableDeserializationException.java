package com.extole.evaluateable.ecma;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class Js2025EvaluatableDeserializationException extends MismatchedInputException {
    private final Exception cause;

    public Js2025EvaluatableDeserializationException(JsonParser parser,
        Exception cause) {
        super(parser, cause.getMessage());
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return super.getOriginalMessage();
    }

    @Override
    public synchronized Exception getCause() {
        return cause;
    }
}
