package com.extole.evaluateable.handlebars;

public class HandlebarsExpressionCompileTimeException extends Exception {
    private final String expression;

    HandlebarsExpressionCompileTimeException(String message, String expression, Throwable throwable) {
        super(message, throwable);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
