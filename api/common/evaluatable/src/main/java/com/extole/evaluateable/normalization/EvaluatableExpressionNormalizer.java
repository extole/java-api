package com.extole.evaluateable.normalization;

import com.extole.evaluateable.ExpressionEvaluatableType;

public interface EvaluatableExpressionNormalizer {

    EvaluatableExpressionNormalizer DEFAULT_NOOP = (expression, type) -> expression;

    String normalize(String expression, ExpressionEvaluatableType type);
}
