package com.extole.evaluateable.javascript;

public class JavascriptExpressionCompileTimeException extends Exception {
    private final String expression;

    JavascriptExpressionCompileTimeException(String message, String expression, Throwable throwable) {
        super(message, throwable);
        this.expression = expression;
    }

    JavascriptExpressionCompileTimeException(String message, String expression) {
        super(message);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

}
