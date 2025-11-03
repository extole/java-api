package com.extole.evaluateable.validation;

import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.ecma.Js2025ExpressionCompileTimeException;
import com.extole.evaluateable.ecma.Js2025ExpressionInvalidLengthException;
import com.extole.evaluateable.handlebars.HandlebarsExpressionCompileTimeException;
import com.extole.evaluateable.spel.SpelExpressionCompileTimeException;

public interface EvaluatableValidator {

    EvaluatableValidator DEFAULT_NOOP = evaluatable -> {};

    void validate(Evaluatable<?, ?> evaluatable)
        throws SpelExpressionCompileTimeException, Js2025ExpressionInvalidLengthException,
        Js2025ExpressionCompileTimeException, HandlebarsExpressionCompileTimeException;
}
