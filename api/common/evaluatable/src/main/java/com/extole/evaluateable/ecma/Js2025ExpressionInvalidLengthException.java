package com.extole.evaluateable.ecma;

public class Js2025ExpressionInvalidLengthException extends Exception {

    private final String expression;
    private final int maxLength;

    Js2025ExpressionInvalidLengthException(String expression, int maxLength) {
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
