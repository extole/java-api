package com.extole.evaluateable.spel;

public class SpelExpressionInvalidLengthException extends Exception {

    private final String expression;
    private final int maxLength;

    SpelExpressionInvalidLengthException(String expression, int maxLength) {
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
