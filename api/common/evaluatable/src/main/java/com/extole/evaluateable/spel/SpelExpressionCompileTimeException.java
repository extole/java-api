package com.extole.evaluateable.spel;

public class SpelExpressionCompileTimeException extends Exception {
    private final String expression;

    SpelExpressionCompileTimeException(String message, String expression, Throwable throwable) {
        super(message, throwable);
        this.expression = expression;
    }

    SpelExpressionCompileTimeException(String message, String expression) {
        super(message);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
