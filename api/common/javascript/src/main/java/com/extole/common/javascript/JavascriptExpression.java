package com.extole.common.javascript;

public class JavascriptExpression {

    private final String expression;

    public JavascriptExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public JavascriptExpression wrapInAnonymousFunction() {
        return new JavascriptExpression("(function () {\n " + expression + "; \n})();");
    }

    public JavascriptExpression wrapInAnonymousFunctionWithReturnValue() {
        return new JavascriptExpression("(function () { return " + expression + "; })();");
    }

    @Override
    public String toString() {
        return expression;
    }

}
