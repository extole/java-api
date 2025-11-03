package com.extole.evaluateable.validation;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ecma.Js2025Evaluatable;
import com.extole.evaluateable.ecma.Js2025ExpressionCompileTimeException;
import com.extole.evaluateable.ecma.Js2025ExpressionInvalidLengthException;
import com.extole.evaluateable.ecma.Js2025ExpressionValidator;
import com.extole.evaluateable.handlebars.HandlebarsEvaluatable;
import com.extole.evaluateable.handlebars.HandlebarsExpressionCompileTimeException;
import com.extole.evaluateable.handlebars.HandlebarsExpressionValidator;
import com.extole.evaluateable.javascript.JavascriptExpressionValidator;
import com.extole.evaluateable.spel.SpelEvaluatable;
import com.extole.evaluateable.spel.SpelExpressionCompileTimeException;
import com.extole.evaluateable.spel.SpelExpressionValidator;

public final class DefaultEvaluatableValidator implements EvaluatableValidator {

    public static final DefaultEvaluatableValidator INSTANCE = new DefaultEvaluatableValidator();
    private static final SpelExpressionValidator SPEL_EXPRESSION_VALIDATOR = SpelExpressionValidator
        .getInstance();
    private static final JavascriptExpressionValidator JAVASCRIPT_EXPRESSION_VALIDATOR = JavascriptExpressionValidator
        .getInstance();
    private static final HandlebarsExpressionValidator HANDLEBARS_EXPRESSION_VALIDATOR = HandlebarsExpressionValidator
        .getInstance();
    private static final Js2025ExpressionValidator JS_2025_EXPRESSION_VALIDATOR =
        Js2025ExpressionValidator.getInstance();

    private DefaultEvaluatableValidator() {

    }

    public void validate(Evaluatable<?, ?> evaluatable)
        throws SpelExpressionCompileTimeException, Js2025ExpressionInvalidLengthException,
        Js2025ExpressionCompileTimeException, HandlebarsExpressionCompileTimeException {
        if (evaluatable instanceof HandlebarsEvaluatable<?, ?> handlebars) {
            validateHandlebarsExpression(handlebars.getExpression());
        }
        if (evaluatable instanceof SpelEvaluatable<?, ?> spel) {
            validateSpelExpression(spel.getExpression());
        }
        if (evaluatable instanceof Js2025Evaluatable<?, ?> js2025) {
            validateJs2025Expression(js2025.getExpression());
        }
    }

    private void validateSpelExpression(String expression)
        throws SpelExpressionCompileTimeException {
        SPEL_EXPRESSION_VALIDATOR.validate(expression);

    }

    private void validateHandlebarsExpression(String expression) throws HandlebarsExpressionCompileTimeException {
        HANDLEBARS_EXPRESSION_VALIDATOR.validate(expression);
    }

    private void validateJs2025Expression(String expression)
        throws Js2025ExpressionInvalidLengthException, Js2025ExpressionCompileTimeException {

        JS_2025_EXPRESSION_VALIDATOR.validate(expression);
    }
}
