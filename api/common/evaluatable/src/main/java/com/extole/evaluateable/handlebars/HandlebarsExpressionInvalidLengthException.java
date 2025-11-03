package com.extole.evaluateable.handlebars;

public class HandlebarsExpressionInvalidLengthException extends Exception {

    private final String expression;
    private final int maxLength;

    HandlebarsExpressionInvalidLengthException(String expression, int maxLength) {
        super("Expression can't be blank or longer than " + maxLength + " characters. Expression: " + expression);
        this.expression = expression;
        this.maxLength = maxLength;
    }

    public String getExpression() {
        return expression;
    }

    public int getMaxLength() {
        return maxLength;
    }

}
