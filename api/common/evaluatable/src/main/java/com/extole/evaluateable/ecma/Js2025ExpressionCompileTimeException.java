package com.extole.evaluateable.ecma;

public class Js2025ExpressionCompileTimeException extends Exception {
    private final String expression;

    Js2025ExpressionCompileTimeException(String message, String expression, Throwable throwable) {
        super(message, throwable);
        this.expression = expression;
    }

    Js2025ExpressionCompileTimeException(String message, String expression) {
        super(message);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
